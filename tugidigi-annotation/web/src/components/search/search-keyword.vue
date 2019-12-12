<style lang="scss">

</style>

<template>
  <input
    type="text"
    class="search-keyword-input"
    v-model="keyword"
    v-on:keyup.prevent.esc="clear()"
  />
</template>

<script lang="ts">
import SearchStore from "components/search/search-store";
import { MyVue } from "types";
import { split } from "utils/misc";
import Vue from "vue";
import Component from "vue-class-component";
import { Prop } from "vue-property-decorator";
export default interface SearchKeyword extends MyVue {}

@Component({
  name: "SearchKeyword",
  components: {}
})
export default class SearchKeyword extends Vue {
  //props
  @Prop() ss: SearchStore<any>;

  keyword: string = "";

  focus() {
    (<HTMLElement>this.$el).focus();
  }

  clear() {
    if (this.keyword) this.keyword = "";
    else this.$emit("escape");
  }

  beforeMount() {
    this.ss.addQueryListenr(ss => {
      if (ss.keywords) {
        this.keyword = ss.keywords.join(" ");
      } else {
        this.keyword = "";
      }
    });
    this.ss.addBeforeSearchListenr((q, detailed) => {
      this.ss.keywords = split(this.keyword);
    });
  }
}
</script>