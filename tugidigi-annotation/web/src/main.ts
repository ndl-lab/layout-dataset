import Buefy from "buefy";
import Router from "router.vue";
import "tslib";
import Vue from "vue";
import "./styles/main.scss";

require("utils/polyfills");

if (process.env.NODE_ENV !== "production") {
  Vue.config.devtools = true;
  Vue.config.performance = true;
}

Vue.use(Buefy);

require("types");

new Router().$mount("#app");
