<style lang="scss">
@import "_palette.scss";

.search-paginaition {
  @include is-mobile() {
    padding: 1rem;
  }
  @include is-tablet() {
    margin: 1.75rem 2.75rem;
  }
  .is-current {
    pointer-events: none;
  }
}

</style>

<template>
  <div class="search-paginaition">
    <nav class="pagination is-small" role="navigation" aria-label="pagination">
      <a class="pagination-previous is-small" v-on:click="goto(page-1)" :disabled="page<=1">
        <b-icon icon="chevron-left"></b-icon>
      </a>
      <a class="pagination-next is-small" v-on:click="goto(page+1)" :disabled="page>=maxPage">
        <b-icon icon="chevron-right"></b-icon>
      </a>
      <ul class="pagination-list">
        <template v-if="show1">
          <li>
            <a class="pagination-link" v-on:click="goto(1)" aria-label="Goto page 1">{{ 1 }}</a>
          </li>
          <li v-show="pageArray[0]>2">
            <span class="pagination-ellipsis">&hellip;</span>
          </li>
        </template>
        <li v-for="p in pageArray">
          <a
            class="pagination-link"
            :class="{'is-current':p===page}"
            :aria-label="'Goto page '+p"
            v-on:click="goto(p)"
          >{{ p }}</a>
        </li>
        <template v-if="show2">
          <li v-show="pageArray[pageArray.length-1]<maxPage-1">
            <span class="pagination-ellipsis">&hellip;</span>
          </li>
          <li>
            <a
              class="pagination-link"
              v-on:click="goto(maxPage)"
              :aria-label="'Goto page '+maxPage"
            >{{ maxPage }}</a>
          </li>
        </template>
      </ul>
    </nav>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import Component from "vue-class-component";
import SearchStore from "components/search/search-store";

import { Prop } from "vue-property-decorator";

@Component({
  name: "SearchPagination"
})
export default class SearchPagination extends Vue {
  @Prop({ default: 2 })
  size: number;
  @Prop()
  ss: SearchStore<any>;

  handleScroll() {
    var scrollGrid = document.getElementsByClassName("grid").item(0);
    if (scrollGrid != null) {
      window.addEventListener("scroll", () => {});
    }
  }

  goto(page) {
    if (page < 0) page = 0;
    if (page > this.maxPage) page = this.maxPage;
    this.ss.setFrom(this.ss.size * (page - 1));
  }

  get page() {
    return Math.floor(this.ss.from / this.ss.size) + 1;
  }

  get show1() {
    return this.page - this.size > 1;
  }

  get show2() {
    return this.page < this.maxPage - this.size && this.maxPage < 20;
  }

  get pageArray() {
    let arr = [];
    for (let i = this.page - this.size; i < this.page + this.size + 1; i++) {
      if (i > 0 && i <= this.maxPage) arr.push(i);
    }
    return arr;
  }

  get maxPage() {
    return Math.ceil(this.ss.hit / this.ss.size);
  }
}
</script>