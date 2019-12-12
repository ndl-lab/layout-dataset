<style lang="scss">
@import "_palette.scss";
.search-detail {
  // .search-detail-field {
  //     border-bottom: .5px solid #d8d8d8;
  // }
  .search-detail-field {
    border-bottom: 0.5px solid $border-grey;
    &:last-child {
      border-bottom: none;
    }
    padding: 0.5rem 0;
    @include is-tablet {
      display: grid;
      grid-template-columns: 1fr 4fr;
      .search-dimentions {
        margin-top: 0.75rem;
      }
    }
    .search-dimentions {
      label {
        padding: 0.5rem;
      }
    }
  }
  .search-field-name {
    font-weight: bold;
    margin-top: 0.75rem;
    margin-bottom: 0.5rem;
  }

  .b-checkbox.checkbox + .checkbox {
    margin-left: 0;
  }
}
</style>

<template>
   <div class="search-detail">
      <div
        class="search-detail-field"
        v-for="(f,ind) in ss.searchdef.searchFields"
      >
        <div class="search-field-name is-size-7-touch">{{ $ls(f.name) }}</div>
        <div class="search-dimentions">
          <component
            :is="searchFieldComponent(f)"
            :field="f"
            :ss="ss"
          ></component>
        </div>
      </div>
    </div>
</template>

<script lang="ts">
import SearchStore from "components/search/search-store";
import { MyVue } from "types";
import Vue from "vue";
import Component from "vue-class-component";
import { Prop } from "vue-property-decorator";
export default interface SearchDetail extends MyVue {}

@Component({
  name: "SearchDetail",
  components: {},
  template: `
  
  `
})
export default class SearchDetail extends Vue {
  @Prop()
  ss: SearchStore<any>;
}
</script>