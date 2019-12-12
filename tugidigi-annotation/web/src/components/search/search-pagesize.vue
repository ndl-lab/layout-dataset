<style lang="scss">

</style>

<template>
<div class="search-pagesize is-size-7">
  <!-- <label>{{$l("label-pagesize")}}</label> -->
  <div v-if="manual" class="field has-addons">
    <div class="control">
      <input class="input" type="number" v-model="size" />
    </div>
    <div class="control">
      <a class="button is-info" @click="go()">{{ $l("l-apply") }}</a>
    </div>
  </div>
  <div v-else class="select">
    <select :value="size" v-on:input="sizeChange">
      <option v-for="v in values" :value="v">{{
        $lt("l-num-of-rows", v)
      }}</option>
    </select>
  </div>
</div>
   
</template>

<script lang="ts">
import SearchStore from "components/search/search-store";
import Vue from "vue";
import Component from "vue-class-component";
import { Prop } from "vue-property-decorator";
import { MyVue } from "types";
export default interface SearchPagesize extends MyVue {}

@Component({
  name: "SearchPagesize",
  
})
export default class SearchPagesize extends Vue {
  @Prop({ default: false })
  manual: boolean;

  @Prop()
  ss: SearchStore<any>;

  innnerSize = 0;
  values = ["10", "20", "50"];

  sizeChange(event) {
    this.ss.setSize(event.target.value);
    this.$emit("size-change", event.target.value);
  }

  get size() {
    return this.ss.size;
  }

  go() {
    this.ss.setSize(this.innnerSize);
  }

  set size(s) {
    this.innnerSize = s;
  }

  created() {}
}

</script>