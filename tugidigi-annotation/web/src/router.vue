<style lang="scss">
</style>

<template>
  <div>
    <div>
      <router-view></router-view>
    </div>
  </div>
</template>

<script lang="ts">
import Component from "vue-class-component";
import Vue from "vue";
import VueRouter from "vue-router";
import { MyVue } from "./types";
import BinderPage from "./pages/binder/binder.vue";
import ListPage from "./pages/list.vue";
import { state } from "./model/state";
import { lsGet, lsSet } from "./service/local-storage-service";
import { BASE_PATH } from "./config";

Vue.use(VueRouter);

let router: VueRouter = new VueRouter({
  mode: "history",
  base: BASE_PATH + "app/",
  scrollBehavior: (to, from, savedPosition: { x: number; y: number }) => {
    if (savedPosition) {
      return savedPosition;
    } else {
      // new navigation.
      let position = { x: undefined, y: undefined, selector: undefined };
      // scroll to anchor by returning the selector
      if (to.hash) {
        position.selector = to.hash;
      }
      if (to.name !== from.name) {
        position.x = 0;
        position.y = 0;
      }
      return position;
    }
  },
  routes: [
    {
      path: "/",
      name: "top",
      component: ListPage,
      meta: { scrollToTop: true }
    },
    { path: "/binder/:id/:pg", name: "binder", component: BinderPage },
    { path: "*", redirect: { name: "top" } }
  ]
});

export default interface Router extends MyVue {}

@Component({
  router: router,
  components: {}
})
export default class Router extends Vue {
  mounted() {
    state.user = lsGet("anno-user");
    this.$setLoading(false);
    if (!state.user) {
      this.$buefy.dialog.prompt({
        message: "ユーザー名を入力してください",
        onConfirm: value => {
          state.user = value;
          lsSet("anno-user", value);
        }
      });
    }
  }
}
</script>