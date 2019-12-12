<style lang="scss">
@import "_palette.scss";
.search-box {
  border-bottom: 1px solid $grey-light;
  .detail-button-search {
    border: solid 1px #391b64;
  }
  .search-title {
    font-size: x-large;
    font-weight: bold;
    margin-bottom: 0.5rem;
  }
  p.description {
    margin-bottom: 1rem;
  }
  &:not(.simple) {
    .keyword {
      @include is-mobile() {
        padding: 0.5rem 0.5rem 0.5rem 1rem;
      }
      @include is-tablet() {
        padding-bottom: 0.5rem;
      }
    }
  }
  &.simple {
    padding: 0;
  }
  .keyword {
    input {
      box-shadow: none;
      background: none;
      border: none;
    }
    .has-icons-left.not {
      .not-input {
        width: 11em;
        border-left: 1px solid $grey-light;
      }
      .material-icons {
        color: $black;
      }
    }
    .keyword-input {
      padding-left: 0;
    }
    .select.and-or {
      @include is-mobile() {
        display: none;
      }
      margin: 0 0.75rem;
      select {
        border-radius: 1.125rem;
        border: 1px solid $grey-light;
      }
    }
    button.is-search {
      background: none;
      border: none;
      i {
        font-size: 2rem;
      }
      @include is-mobile {
        padding: 0.25rem;
      }
    }
    a.button.details {
      border: 1px solid $grey-light;
    }
    input:placeholder-shown,
    input::-webkit-input-placeholder {
      color: $grey-dark;
    }
  }
  .search-fields {
    padding: 1rem;
    @include is-mobile {
      padding: 0.5rem;
    }
    max-width: 64rem;
    margin: 0 auto;
    .search-field {
      margin: 0.5rem calc(0.75em - 1px) 1rem;
    }
    label.b-checkbox {
      padding: 0.5rem;
    }
    .b-checkbox.checkbox + .checkbox {
      margin-left: 0;
    }
    select {
      border: $basic-border;
    }
    input {
      box-shadow: none;
      margin: 0 0 0.5rem;
      background: $white;
      border: $basic-border;
    }
    .search-field-name {
    }
  }
  .not {
    @include is-mobile() {
      display: none;
    }
  }
  .field.has-addons .control.is-expanded {
    display: flex;
    align-items: center;
  }
}

</style>

<template>
<div class="search-box" :class="{'simple':simple}">
  <div v-if="!noheader&&!simple" class="jps-searchdef">
    <h4 class="search-title title is-size-7-touch is-size-5-desktop has-text-centered">
      {{ $ls(searchdef.name) }}
    </h4>
    <p class="description has-text-centered">
      {{ $ls(searchdef.description) }}
    </p>
  </div>
  <form v-on:submit.prevent="doSearch()" action="/">
    <div class="keyword" v-if="hasKeywordField">
      <div class="field has-addons">
        <p class="control is-expanded">
          <input
            class="input keyword-input is-size-6-touch"
            :placeholder="placeholder"
            type="search"
            v-model="keyword"
            v-on:keyup.prevent.esc="keyword=''"
          />
        </p>
        <template v-if="!simple">
          <p class="control">
            <span class="select and-or">
              <select v-model="keywordType">
                <option value="AND">{{ $l("l-keyword-and") }}</option>
                <option value="OR">{{ $l("l-keyword-or") }}</option>
              </select>
            </span>
          </p>
          <p class="control has-icons-left not">
            <input
              class="input not-input"
              :placeholder="$l('l-placeholder-not-keyword')"
              type="search"
              v-model="notKeyword"
              v-on:keyup.prevent.esc="notKeyword=''"
            />
            <span class="icon is-small is-left">
              <i class="material-icons notranslate" aria-hidden="true"> cancel </i>
            </span>
          </p>
        </template>
        <p class="control">
          <button type="submit" class="search-button button is-search">
            <span><i class="material-icons notranslate">search</i></span>
          </button>
        </p>
        <p class="control" v-if="hasSearchFields">
          <a class="button details" v-on:click="showDetail = !showDetail"
            >{{ $l("l-detail")
            }}<i class="material-icons notranslate">{{
              showDetail ? "arrow_drop_up" : "arrow_drop_down"
            }}</i></a
          >
        </p>
      </div>
    </div>
    <div class="search-fields" v-show="detailed || showDetail">
      <div class="search-field" v-for="(f,ind) in searchdef.searchFields">
        <div class="search-field-name is-size-7 ">{{ $ls(f.name) }}</div>
        <component
          :is="searchFieldComponent(f)"
          :field="f"
          :ss="ss"
        ></component>
      </div>
      <div class="detailed-search" v-if="detailed">
        <button
          type="submit"
          class="search-button button is-search is-fullwidth"
        >
          <span><i class="material-icons notranslate">search</i></span>
        </button>
      </div>
      <div class="has-text-centered detail-buttons">
        <a class="button is-rounded detail-button-close" v-on:click="showDetail = !showDetail">検索条件を閉じる</a>
        <button type="submit" class="button is-info is-rounded search-button detail-button-search">検索する</button>
      </div>
    </div>
  </form>
</div>
   
</template>

<script lang="ts">
import SearchStore from "components/search/search-store";
import { MyVue } from "types";
import Vue from "vue";
import Component from "vue-class-component";
import { Prop } from "vue-property-decorator";

export default interface SearchBox extends MyVue {}

@Component({
  name: "SearchBox",
  components: {},
  
})
export default class SearchBox extends Vue {
  //props
  @Prop() ss: SearchStore<any>;

  @Prop({ default: false })
  noheader: boolean;

  @Prop({ default: false })
  simple: boolean;

  @Prop({ default: false })
  detailed: boolean;

  @Prop({ default: false })
  initialdetail: boolean;

  showDetail: boolean = false;

  keyword: string = "";

  notKeyword: string = "";

  keywordType: "AND" | "OR" = "AND";

  focus() {
    const inputs = this.$el.getElementsByClassName("keyword-input");

    if (inputs) {
      this.$nextTick(() => {
        (<HTMLElement>inputs[0]).focus();
      });
    }
  }

  beforeMount() {
    this.ss.addQueryListenr(ss => {
      if (ss.keywords) {
        this.keyword = ss.keywords.filter(k => !k.startsWith("-")).join(" ");
        this.notKeyword = ss.keywords
          .filter(k => k.startsWith("-"))
          .map(k => k.substr(1))
          .join(" ");
      } else {
        this.keyword = "";
        this.notKeyword = "";
      }
      this.keywordType = this.ss.keywordType;
      // this.showDetail = false;
    });
    this.showDetail = this.initialdetail;
  }

  doSearch() {
    this.ss.from = 0;
    this.ss.facet = {};
    if (!this.detailed) {
      let sendKeyword = this.keyword.split(/[\s　]+/);
      if (this.notKeyword)
        sendKeyword.push(...this.notKeyword.split(/[\s　]+/).map(k => "-" + k));
      this.ss.keywords = sendKeyword;
      this.ss.keywordType = this.keywordType;
    }
    this.ss.execute(this.showDetail || this.detailed, r => {
      this.$notifyError("検索エラー");
    });
  }
}

//Lang Resource Start
const localLangResource = {
  "l-placeholder-not-keyword": { ja: "除外キーワード", en: "Not Keywords" },
  "l-keyword-and": { ja: "すべて含む", en: "And" },
  "l-keyword-or": { ja: "どれか含む", en: "Or" },
  "m-search-error": {
    ja: "検索に失敗しました。もう一度お試し下さい。",
    en: "Search failed. Please try again."
  },
  "l-detail": { ja: "詳細", en: "Detail" }
};
//Lang Resource End

</script>