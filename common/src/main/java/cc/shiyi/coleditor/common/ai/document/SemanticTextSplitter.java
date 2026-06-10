package cc.shiyi.coleditor.common.ai.document;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文档切分器，用于将原始文档切分为多个 smaller 文档块，
 * */
@Service
public class SemanticTextSplitter {

    @Autowired
    EmbeddingModel embeddingClient;

    private final static float SIMILARITY_THRESHOLD = 0.75f;
    private final static int MAX_CHUNK_SIZE_TOKENS = 512;

    /**
     * 将一个文档按语义切分为多个较小的文档块。
     * 首先将文档内容按句子切分，然后通过句子 embeddings 的相似度判断语义边界，
     * 并结合最大 token 长度限制进行智能合并，最终生成一组语义连贯的文档块。
     *
     * @param document 待切分的原始文档，包含格式化的内容和元数据
     * @return 切分后的文档块列表，每个块保持原始文档的元数据
     */
    public List<Document> split(Document document) {
        String content = document.getFormattedContent();
        List<String> sentences = splitIntoSentences(content);

        if (sentences.size() <= 1) {
            return Collections.singletonList(document);
        }

        // Step 1: 为每句话生成 embedding
        List<float[]> embeddings = sentences.stream()
                .map(sentence -> embeddingClient.embed(sentence))
                .toList();

        // Step 2: 使用滑动窗口合并高相似度句子
        List<Document> chunks = new ArrayList<>();
        List<String> currentChunk = new ArrayList<>();
        int currentTokenCount = 0;

        for (int i = 0; i < sentences.size(); i++) {
            String currentSentence = sentences.get(i);
            int sentenceTokenLength = estimateTokenLength(currentSentence);

            // 超出最大长度则强制切分
            if (currentTokenCount + sentenceTokenLength > MAX_CHUNK_SIZE_TOKENS && !currentChunk.isEmpty()) {
                flushChunk(chunks, currentChunk, document.getMetadata());
                currentTokenCount = sentenceTokenLength;
            } else {
                currentTokenCount += sentenceTokenLength;
            }

            currentChunk.add(currentSentence);

            // 如果不是最后一句，检查与下一句的相似度
            if (i < sentences.size() - 1) {
                float similarity = cosineSimilarity(embeddings.get(i), embeddings.get(i + 1));
                if (similarity < SIMILARITY_THRESHOLD) {
                    // 差异大 → 是边界 → 提交当前 chunk
                    flushChunk(chunks, currentChunk, document.getMetadata());
                    currentTokenCount = 0;
                }
            }
        }

        // 最后一个 chunk
        if (!currentChunk.isEmpty()) {
            flushChunk(chunks, currentChunk, document.getMetadata());
        }

        return chunks;
    }

    /**
     * 将当前行集合中的内容刷新为一个新的文档块
     * <p>
     * 当行集合不为空时，将所有行通过空格连接成一个字符串，
     * 并使用该字符串和元数据创建一个新的Document对象，
     * 然后将该文档添加到块集合中，并清空当前行集合
     *
     * @param chunks   存储文档块的列表
     * @param lines    当前待处理的行集合
     * @param metadata 文档块的元数据信息
     */
    private void flushChunk(List<Document> chunks, List<String> lines, Map<String, Object> metadata) {
        if (!lines.isEmpty()) {
            String joined = String.join(" ", lines);
            Document chunkDoc = new Document(joined, new HashMap<>(metadata));
            chunks.add(chunkDoc);
            lines.clear();
        }
    }

    /**
     * 将文本按句子分割成多个句子列表
     *
     * @param text 需要分割的原始文本
     * @return 包含所有句子的列表，每个句子作为列表的一个元素
     */
    private List<String> splitIntoSentences(String text) {
        return Arrays.stream(text.split("(?<=[。.!！？?;；])\\s+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 估算文本的token长度
     * 该方法根据经验规则粗略估算给定文本的token数量，
     * 主要用于预估处理成本或设置缓冲区大小
     *
     * @param text 需要估算token长度的文本字符串
     * @return 估算的token数量，向上取整
     */
    private int estimateTokenLength(String text) {
        // 粗略估算：英文约 1 token = 4 chars；中文可视为 1 字 ≈ 1~2 tokens
        return (int) Math.ceil(text.length() / 3.0);
    }

    /**
     * 计算两个向量之间的余弦相似度
     * 余弦相似度是通过计算两个向量夹角的余弦值来衡量它们之间的相似性，值域为[-1, 1]
     * 值为1表示完全相似，值为-1表示完全相反，值为0表示正交无关
     *
     * @param vectorA 第一个向量
     * @param vectorB 第二个向量
     * @return 两个向量的余弦相似度值，范围在[-1, 1]之间
     * @throws IllegalArgumentException 当两个向量长度不相同时抛出异常
     */
    private float cosineSimilarity(float[] vectorA, float[] vectorB) {
        // 确保两个向量长度相同
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("Vectors must have the same length");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        // 计算点积和各自向量的模长平方
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += vectorA[i] * vectorA[i];
            normB += vectorB[i] * vectorB[i];
        }

        // 如果任一向量为零向量，则相似度为0
        if (normA == 0 || normB == 0) {
            return 0.0f;
        }

        // 返回余弦相似度
        return (float) (dotProduct / (Math.sqrt(normA) * Math.sqrt(normB)));
    }

}