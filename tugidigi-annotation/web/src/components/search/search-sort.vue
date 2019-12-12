<style lang="scss">
@import "_palette.scss";

.search-sort {
  .sort-ie {
    display: none;
    @include is-IE() {
      display: block;
      min-width: 4rem;
    }
  }
}
</style>

<template>
  <div class="search-sort is-size-7">
    <!-- <label>{{$l("label-sort")}}</label> -->
    <div class="select">
      <select :value="ss.sort" v-on:input="changeSort">
        <option value>{{ $l("l-sort-default") }}</option>
        <option
          v-if="options"
          v-for="opt in options"
          :value="opt.index+':'+(opt.asccend?'asc':'desc')"
        >{{ $ls(opt.name) }}</option>
      </select>
    </div>
    <button
      type="button"
      @click.stop.prevent="changeSortIE"
      class="sort-ie button is-small"
    >{{ $l("label-sort") }}</button>
  </div>
</template>

<script lang="ts">
import SearchStore from "components/search/search-store";
import { LangString, MyVue } from "types";
import Vue from "vue";
import Component from "vue-class-component";
import { Prop } from "vue-property-decorator";

export default interface SearchSort extends MyVue {}

export interface SortOption {
  name: LangString;
  index: string;
  asccend: boolean;
}

@Component({
  name: "SearchSort"
})
export default class SearchSort extends Vue {
  @Prop() ss: SearchStore<any>;

  @Prop() options: SortOption[];

  created() {}

  changeSort(sort: any) {
    if (sort.target.value) {
      this.ss.setSort([sort.target.value]);
    } else {
      this.ss.setSort(null);
    }
  }

  changeSortIE() {
    let value = this.$el.getElementsByTagName("select")[0].value;
    if (value) {
      this.ss.setSort([value]);
    } else {
      this.ss.setSort(null);
    }
  }
}
</script>