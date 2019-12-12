<style lang="scss">
@import "_palette.scss";

.search-facet {
  .facet-title {
    padding: 0;
    border-bottom: 1px solid grey;
    font-weight: bold;
    overflow: hidden;
    button {
      float: right;
      border: none;
      background: none;
      margin-right: 0.125rem;
      display: flex;
      width: 2rem;
      height: 2rem;
      line-height: 2rem;
      align-items: center;
      color: $grey-darker;
    }
    h3 {
      float: left;
      font-weight: 500;
    }
  }

  .search-facet-facets {
    .search-facet-facet {
      width: 100%;
      display: flex;
      align-items: center;

      .b-checkbox {
        margin-left: 0;
        width: 100%;
        .control-label {
          display: flex;
          align-items: center;
          line-height: 2.5rem;
          width: 100%;
          .value {
            flex-basis: 70%;
            line-height: 1.25rem;
            padding: 0.5rem 0rem;
            // padding-left: 0.5rem;
            display: flex;
            align-items: center;
            i {
              margin: 0;
            }
            i + span {
              padding-left: 0.25rem;
            }
            span {
              @include typo-list();
            }
          }
          .count {
            flex-basis: 30%;
            text-align: center;
            a {
              float: right;
            }
          }
        }
      }

      &:not(:last-child) {
        border-bottom: $basic-border;
      }
      // &.is-selected {
      //   background: $jps-key-blue;
      //   color: $white;
      //   a {
      //     color: $white;
      //   }
      // }
      &.not-facet-selected {
        background: $grey-lighter;
        color: $grey-dark;
        span {
          opacity: 0.65;
        }
        a {
          color: $grey-dark;
        }
      }

      .not-facet {
        i {
          cursor: pointer;
        }
        display: flex;
        width: 2rem;
        height: 2rem;
        align-items: center;
      }
    }
  }

  .show-remain-button {
    padding-top: 0.5rem;
    div {
      cursor: pointer;
      display: flex;
      justify-content: flex-end;
      align-items: center;
      i {
        margin-right: 0.325rem;
        color: $grey-darker;
      }
    }
  }

  .not-facet-selected {
    &:nth-child(3) {
      margin-top: 1rem;
    }
    &:not(:last-child) {
      border-bottom: $basic-border;
    }
    span.control-label {
      padding: 0.5rem;
    }
  }
}

</style>

<template>
  <div class="search-facet" v-if="hasFacet">
    <div class="facet-title">
      <h3 class="is-size-7-touch">{{ name }}</h3>
    </div>
    <div class="search-facet-facets" v-show="showFacet">
      <div
        class="search-facet-facet"
        :class="{'is-selected':isSelected(value)}"
        v-for="(count,value,index) in sortedFacet"
        :key="value"
        v-show="isShown(index)"
      >
        <b-checkbox :value="isSelected(value)" @input="select(value)">
          <span class="value is-size-7-touch" v-if="date">{{ $date(value) }}</span>
          <span class="value is-size-7-touch">{{ value }}</span>
          <span class="count is-size-7-touch">{{ count }}</span>
        </b-checkbox>
        <!-- <div class="not-facet is-size-7-touch">
        <b-icon
          v-if="!isSelected(value)"
          class="material-icons notranslate"
          v-on:click="exclude(value)"
          icon="cancel"
        ></b-icon>
        </div>-->
      </div>
    </div>
    <div class="search-facet-facet is-selected not-facet-selected" v-for="value in notFacets">
      <div class="value is-size-7-touch" v-on:click.prevent="exclude(value)">
        <b-checkbox :indeterminate="true">{{ value }}</b-checkbox>
      </div>
    </div>

    <div
      class="show-remain-button is-size-7-touch"
      @click="showAll=!showAll"
      v-if="isShowHideButton()"
    >
      <div v-if="!showAll">
        <span>表示</span>
        <b-icon icon="menu-down"></b-icon>
      </div>
      <div v-else>
        <span>隠す</span>
        <b-icon icon="menu-up"></b-icon>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import Component from "vue-class-component";
import { Prop } from "vue-property-decorator";
import SearchStore from "components/search/search-store";
import { EsFacet } from "service/search-utils";
import { MyVue } from "types";

export default interface SearchFacet extends MyVue {}
const DEFAULT_LEN = 5;

@Component({
  name: "SearchFacet",

  components: {}
})
export default class SearchFacet extends Vue {
  @Prop({ required: true })
  name: string;
  @Prop({ required: true })
  facet: EsFacet;
  @Prop({ required: true })
  ss: SearchStore<any>;
  @Prop({ default: DEFAULT_LEN })
  facetLength: number;
  @Prop({ default: false })
  date: boolean;

  //Data
  showFacet: boolean = true;
  showAll: boolean = false;

  get hasFacet() {
    return (
      Object.keys(this.facet.counts).length > 0 || this.notFacets.length > 0
    );
  }

  get sortedFacet(): { [key: string]: number } {
    let obj = {};
    let keys = Object.keys(this.facet.counts).filter(k => k !== "various");
    keys = keys.sort((a, b) => this.facet.counts[b] - this.facet.counts[a]);
    keys.forEach(key => {
      obj[key] = this.facet.counts[key];
    });
    return obj;
  }

  created() {}

  async beforeMount() {}

  select(value: string) {
    if (this.isSelected(value)) {
      this.ss.removeFacet(this.facet.field, [value]);
    } else {
      this.ss.addFacet(this.facet.field, [value]);
    }
  }

  exclude(value: string) {
    this.select("-" + value);
  }

  get notFacets(): string[] {
    if (this.ss.facet && this.ss.facet[this.facet.field]) {
      return this.ss.facet[this.facet.field]
        .filter(value => value.startsWith("-"))
        .map(value => value.substring(1));
    }
    return [];
  }

  isShown(index: number) {
    return this.showAll || index < this.facetLength;
  }

  get facetsRemains(): number {
    return Object.keys(this.facet.counts).length - this.facetLength;
  }

  isShowHideButton() {
    return this.facetsRemains > 0;
  }

  isSelected(value: string): boolean {
    if (this.ss.facet && this.ss.facet[this.facet.field]) {
      return this.ss.facet[this.facet.field].includes(value);
    }
    return false;
  }
}
</script>