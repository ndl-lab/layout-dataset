<style lang="scss">
@import "_palette.scss";

html {
  overflow: hidden !important;
}

.binder {
  .binder-toolbar {
    padding-left: 0.5rem;
    display: flex;
    align-items: center;
    padding-bottom: 2px;
    background: #a7adba;
    height: 2.5rem;
    .title-bar {
      font-weight: bold;
    }
    .menu-pages {
      display: flex;
      align-items: center;
      padding-left: 1rem;
      padding-right: 1rem;
    }
    .holder {
      padding-left: 1rem;
    }
  }

  .edit {
    height: calc(100vh - 2.5rem);
  }

  .b-tabs .tab-content {
    padding: 0;
  }
  .bts {
    text-align: center;
    &.is-selected {
      background: gray;
    }
  }
  .listx {
    overflow-x: scroll;
    height: 90vh;
    padding: 0.5rem;
  }
  .image-list {
    display: flex;
    flex-flow: wrap;
    .t-image {
      margin-left: 0.5rem;
      margin-bottom: 0.5rem;
    }
  }
  .taginput-container.is-focusable {
    height: auto !important;
  }
}
</style>

<template>
  <div class="binder" v-if="binder">
    <div class="binder-toolbar" v-if="image && type">
      <span class="title-bar">
        <a :href="binder.seeAlso" target="_blank">{{ binder.id }}/{{ binder.name }}({{ type.name }})</a>
      </span>
      <div class="menu-pages">
        <button class="button" @click="prev()">前へ</button>
        <button class="button" @click="next()">次へ</button>
        {{index+1}}/{{ images.length }}({{ image.id }}/{{image.status}})
      </div>
      <div class="menu-buttons">
        <button class="button" @click="save()">保存</button>
        <button class="button" @click="isListModalActive=true">一覧</button>
        <button class="button" @click="isMetaModalActive=true">メタ</button>
      </div>
      <div class="holder" v-if="binder.holder">現在の作業者：{{binder.holder}}</div>
    </div>
    <rect-editor :type="type" ref="editor"></rect-editor>

    <b-modal :active.sync="isListModalActive" full-screen>
      画像一覧
      <button @click="isListModalActive=false">閉じる</button>
      <div class="image-list" v-if="isListModalActive">
        <router-link
          class="t-image"
          v-for="(i,idx) in images"
          :key="i.id"
          :to="{name:'binder', params:{id:binder.id,pg:idx+1}}"
        >
          <figure class="image">
            <img :src="thumbUrl(i)+'?v='+i.version" />
          </figure>
          {{ i.id }}/{{i.status}}
        </router-link>
      </div>
    </b-modal>

    <b-modal :active.sync="isMetaModalActive" scroll="keep">
      メタデータ
      <div class="card">
        <button class="button" @click="ok()">全部OK</button>
        <b-tabs>
          <b-tab-item label="タグ等">
            <table class="table is-fullwidth">
              <tbody>
                <tr>
                  <td>{{ binder.status }}</td>
                </tr>
                <tr>
                  <td>
                    <b-field>
                      <b-taginput v-model="binder.tags" icon="label" placeholder="タグの追加"></b-taginput>
                    </b-field>
                  </td>
                </tr>
                <tr>
                  <td class="has-text-centered">
                    <button class="button" @click="saveBinder()">保存</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </b-tab-item>
          <b-tab-item label="履歴" class="listx">
            <table class="table">
              <tr v-for="(a,i) in binder.actions" :key="i">
                <td>{{ a.type }}</td>
                <td>{{ $dateTime(a.start) }}</td>
                <td>{{ a.note }}</td>
              </tr>
            </table>
          </b-tab-item>
        </b-tabs>
      </div>
    </b-modal>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import Component from "vue-class-component";
import { ImageBinder } from "domain/imagebinder";
import { TargetImage } from "domain/targetimage";
import { ImageType } from "domain/imagetype";
import {
  getImage,
  searchImage,
  saveImage,
  thumbUrl
} from "service/image-service";
import { getBinder, saveBinder } from "service/binder-service";
import { getType } from "service/type-service";
import { MyVue } from "types";

import RectEditor from "./rect-editor.vue";
import { Ref, Watch } from "vue-property-decorator";
import { state } from "../../model/state";

export default interface BinderPage extends MyVue {}
@Component({
  components: { RectEditor }
})
export default class BinderPage extends Vue {
  binder: ImageBinder = null;
  images: TargetImage[] = [];
  index: number = 0;
  type: ImageType = null;

  @Ref("editor")
  editor: RectEditor;

  async beforeMount() {
    this.binder = (await getBinder(this.$route.params["id"])).data;
    this.images = (await searchImage({
      size: 1000,
      filter: { binder: [this.binder.id] },
      sort: ["id"]
    })).data.list;
    this.type = (await getType(this.binder.imageType)).data;
    this.$nextTick(() => {
      this.setImage();
    });
    if (
      this.binder &&
      !this.binder.holder &&
      this.binder.status !== "CHECKED"
    ) {
      this.$buefy.dialog.confirm({
        message: "この資料を担当しますか？",
        onConfirm: () => {
          this.binder.holder = state.user;
          saveBinder(this.binder).then(r => {
            this.binder = r.data;
          });
        }
      });
    }
  }

  ok() {
    this.binder.status = "CHECKED";
    this.binder.actions.push({
      type: "CHECKED",
      start: new Date().getTime(),
      user: this.binder.holder
    });
    this.binder.holder = null;
    saveBinder(this.binder).then(r => {
      this.binder = r.data;
    });
  }

  thumbUrl(img) {
    return thumbUrl(img);
  }

  @Watch("$route")
  watchRoute() {
    this.setImage();
    this.isListModalActive = false;
  }

  setImage() {
    this.index = Number(this.$route.params["pg"]) - 1;
    if (this.image) this.editor.setImage(this.image);
  }

  isListModalActive: boolean = false;
  isMetaModalActive: boolean = false;

  saveBinder() {
    saveBinder(this.binder).then(r => {
      this.binder = r.data;
    });
  }

  save() {
    if (this.editor && this.editor.i) {
      this.$setLoading(true);
      const i = this.editor.getResult();
      i.status = "ANNOTATED";
      const curentIndex = this.images.findIndex(im => im.id === i.id);
      saveImage(i).then(r => {
        this.images[curentIndex] = r.data;
        this.$setLoading(false);
      });
    }
  }

  show(i: number, save = true) {
    if (save) this.save();
    this.$router.push({
      name: "binder",
      params: { id: this.binder.id, pg: String(i + 1) }
    });
  }

  get image() {
    if (this.images.length > 0) return this.images[this.index];
    return null;
  }

  next() {
    if (this.index === this.images.length - 1) {
      this.save();
      return;
    }
    this.show(this.index + 1);
  }

  prev() {
    if (this.index === 0) {
      this.save();
      return;
    }
    this.show(this.index - 1);
  }
}
</script>