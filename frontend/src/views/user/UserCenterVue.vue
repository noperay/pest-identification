<script setup>
import ChangeUserAvatar from '@/views/user/ChangeUserAvatar.vue'
import ChangeUserInfo from '@/views/user/ChangeUserInfo.vue'
import ChangeUserPassword from '@/views/user/ChangeUserPassword.vue'
</script>

<template>
  <div class="UserCenter flex">
    <div class="UserInfo">
      <ChangeUserInfo></ChangeUserInfo>
    </div>
    <div class="UserAvatar">
      <ChangeUserAvatar></ChangeUserAvatar>
    </div>
  </div>
  <ChangeUserPassword></ChangeUserPassword>
</template>

<style scoped>
.UserCenter {
  width: 100%;
}

.UserInfo {
  /* border: 1px solid red; */
  width: 650px;
}

.UserAvatar {
  /* border: 1px solid brown; */
  width: 570px;
}
</style>