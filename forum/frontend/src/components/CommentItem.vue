<template>
  <div style="padding: 10px 0; border-bottom: 1px solid #eee">
    <!-- 作者和内容 -->
    <div v-if="showAuthor" style="display: flex; gap: 8px; align-items: center; margin-bottom: 4px">
      <span style="font-weight: 500; font-size: 13px">{{ authorName }}</span>
      <span style="color: #909399; font-size: 12px">{{ formatTime(comment.createdTime) }}</span>
    </div>
    <p style="margin: 0">{{ comment.content }}</p>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 4px">
      <span v-if="!showAuthor" style="color: #909399; font-size: 12px">{{ formatTime(comment.createdTime) }}</span>
      <span v-else />
      <el-button text size="small" type="primary" @click.stop="$emit('reply', comment)">回复</el-button>
    </div>
    <!-- 子回复 -->
    <div v-if="children.length" style="margin-left: 28px; margin-top: 4px; padding-left: 12px; border-left: 2px solid #e4e7ed">
      <CommentItem
        v-for="child in children"
        :key="child.id"
        :comment="child"
        :all-comments="allComments"
        :show-author="showAuthor"
        :author-names="authorNames"
        @reply="(c: any) => $emit('reply', c)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { ForumComment } from '@/api/forum'
import { formatTime } from '@/utils/format'

const props = defineProps<{
  comment: ForumComment
  allComments: ForumComment[]
  showAuthor?: boolean
  authorNames?: Record<number, string>
}>()

defineEmits<{
  reply: [comment: ForumComment]
}>()

const children = computed(() =>
  props.allComments.filter(c => c.replyTo === props.comment.id)
)

const authorName = computed(() => {
  if (!props.authorNames) return ''
  return props.authorNames[props.comment.authorId] || ('用户' + props.comment.authorId)
})
</script>
