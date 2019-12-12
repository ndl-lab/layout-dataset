<style lang="scss">
@import "_palette.scss";
.book-list {
  padding-top: 1rem;
  padding-bottom: 1rem;
  .search-box {
    display: flex;
    justify-content: center;
    .rec-button {
      margin-left: 4rem;
    }
    height: 3rem;
  }
  img {
    width: 40px !important;
  }
  .item-search-result {
    height: calc(100vh - 5rem);
    display: flex;
    flex-direction: column;
    padding-left: 1rem;
    padding-right: 1rem;
  }
  .search-nav {
    margin-bottom: 0;
  }
  .result-list {
    display: flex;
    flex-grow: 1;

    .facet {
      padding-right: 1rem;
      width: 12rem;
      .search-facet {
        &:not(:first-child) {
          margin-top: 1rem;
        }
      }
    }
    .table {
      width: calc(100% - 12rem);
    }
  }

  .result-list {
    padding-top: 1rem !important;
    overflow-y: scroll;
  }

  .search-result-body {
    background: white;
    padding: 2rem;
  }
}
</style>

<template>
  <div class="book-list">
    <div class="search-box">
      <form v-on:submit.prevent="doSearch()">
        <div class="field has-addons">
          <p class="control">
            <input
              class="input keyword-input is-size-6-touch"
              type="search"
              v-model="keyword"
              v-on:keyup.prevent.esc="keyword=''"
            />
          </p>
          <p class="control">
            <button type="submit" class="search-button button is-search">
              <b-icon icon="magnify"></b-icon>
            </button>
          </p>
        </div>
      </form>
    </div>
    <div class="item-search-result" v-if="ss.result">
      <nav class="search-nav level">
        <div class="level-left">
          <!-- ヒット件数 -->
          <div class="level-item is-size-7-touch">{{ $num(ss.result.hit) }} 件</div>
          <search-pagination :ss="ss"></search-pagination>
        </div>
      </nav>
      <div class="result-list">
        <div class="facet">
          <search-facet name="ステータス" :facet="ss.result.facets['status']" :ss="ss"></search-facet>
          <search-facet name="種別" :facet="ss.result.facets['imageType']" :ss="ss"></search-facet>
          <search-facet name="タグ" :facet="ss.result.facets['tags']" :ss="ss"></search-facet>
          <search-facet name="作業者" :facet="ss.result.facets['holder']" :ss="ss"></search-facet>
        </div>
        <table class="table is-fullwidth">
          <thead>
            <tr>
              <th>タイトル</th>
              <th>ステータス</th>
              <th>タグ</th>
              <th>作業者</th>
              <th>画像枚数</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="v in ss.result.list" :key="v.id">
              <td>
                <router-link :to="{name:'binder', params:{id:v.id,pg:1}}">
                  {{
                  v.name
                  }}
                </router-link>
              </td>
              <td>{{ v.status }}</td>
              <td>{{ $join(v.tags) }}</td>
              <td>{{ v.holder }}</td>
              <td>{{ v.images }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { ImageBinder } from "domain/imagebinder";
import { searchBinder } from "service/binder-service";
import SearchPagesize from "components/search/search-pagesize.vue";
import SearchPagination from "components/search/search-pagination.vue";
import SearchSort from "components/search/search-sort.vue";
import SearchStore from "components/search/search-store";
import SearchFacet from "components/search/search-facet.vue";
import Vue from "vue";
import Component from "vue-class-component";

var VueScrollTo = require("vue-scrollto");

@Component({
  components: {
    SearchPagination,
    SearchFacet
  }
})
export default class ListPage extends Vue {
  ss: SearchStore<ImageBinder> = null;
  activeTab: number = 0;
  showFacet: boolean = false;
  showThumbs: boolean = false;

  beforeMount() {
    this.ss = new SearchStore(searchBinder, true);
  }

  mounted() {
    this.ss.restoreQuery();
  }

  keyword: string = "";

  doSearch() {
    let kw = this.keyword.split(/[\s　]+/g);
    this.ss.query = { name: kw };
    this.ss.keywordType = "OR";
    this.ss.from = 0;
    this.ss.execute();
  }
}
</script>