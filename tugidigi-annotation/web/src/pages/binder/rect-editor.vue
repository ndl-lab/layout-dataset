<style lang="scss">
@import "_palette.scss";
.edit {
  .editor-toolbar {
    display: flex;
    align-items: center;
    background: #c0c5ce;
    height: 2.5rem;
    .group {
      padding-left: 0.5rem;
      padding-right: 0.5rem;
      align-items: center;
      display: flex;
    }

    .bw-slider {
      width: 10rem;
    }
  }
  .editor-main {
    height: calc(100% - 2.5rem);
    display: flex;

    .lefta {
      width: 8rem;
      min-width: 8rem;
      .annotations {
        height: calc(100vh - 15rem);

        overflow: scroll;

        .annot-selected {
          background: $yellow;
        }
      }
      .note {
        textarea {
          width: 100%;
          height: 100%;
        }
        height: 10rem;
      }
    }
    #container {
      overflow: scroll;
    }
  }
}
</style>

<template>
  <div class="edit" v-show="i">
    <div class="editor-toolbar">
      <div class="group">
        <span>モード</span>
        <b-field>
          <b-radio-button v-model="func" native-value="draw">
            <b-icon icon="pencil-plus"></b-icon>
            <span>追加</span>
          </b-radio-button>

          <b-radio-button v-model="func" native-value="select">
            <b-icon icon="arrow-all"></b-icon>
            <span>選択</span>
          </b-radio-button>

          <b-radio-button v-model="func" native-value="cut">
            <b-icon icon="scissors-cutting"></b-icon>
            <span>裁断</span>
          </b-radio-button>

          <b-radio-button v-model="func" native-value="fit">
            <b-icon icon="arrow-collapse-all"></b-icon>
            <span>Fit</span>
          </b-radio-button>
          <b-radio-button v-model="func" native-value="set">
            <b-icon icon="label"></b-icon>
            <span>ラベル</span>
          </b-radio-button>

          <b-radio-button v-model="func" native-value="all">
            <b-icon icon="wallpaper"></b-icon>
            <span>全体</span>
          </b-radio-button>
          <b-radio-button v-model="func" native-value="erase">
            <b-icon icon="eraser"></b-icon>
            <span>削除</span>
          </b-radio-button>
        </b-field>
      </div>
      <div class="group" v-show="func!=='fit'">
        <b-checkbox v-model="autoFit">AutoFit</b-checkbox>
      </div>
      <div class="group" v-show="func==='fit'">
        <button class="button is-small" @click="fullFit()">Full Fit</button>
      </div>
      <div class="group" v-if="func==='cut'">
        <b-field>
          <b-radio v-model="cutType" native-value="vertical">
            <span>垂直</span>
          </b-radio>
          <b-radio v-model="cutType" native-value="horizontal">
            <span>水平</span>
          </b-radio>
        </b-field>
      </div>
      <div class="group" v-if="func!=='all'">
        <b-field>
          <b-select v-model="currentType">
            <option v-for="option in types" :value="option.id" :key="option.id">{{ option.id }}</option>
          </b-select>
        </b-field>
      </div>
      <div class="group">
        <b-checkbox v-model="filteEnabled" @input="setFilter()">二値化</b-checkbox>
        <b-slider
          class="bw-slider"
          v-model="threshold"
          :min="50"
          :max="255"
          @input="debouncedFilter()"
          @change="debouncedFilter()"
        ></b-slider>
      </div>
      <div class="group">
        <b-slider
          class="bw-slider"
          v-model="scaleBar"
          :min="1"
          :max="200"
          @input="debouncedScale()"
          @change="debouncedScale()"
        ></b-slider>
      </div>
      <div class="group">
        <auto-saver ref="autosaver" v-show="func!='all'" v-model="dorects" />
      </div>
      <!-- <div class="group">
        <button class="button is-small" @click="showNote()">
          <b-icon icon="pencil" />
        </button>
      </div>-->
    </div>
    <div class="editor-main">
      <div class="lefta">
        <div class="annotations">
          <div
            v-for="a in rects"
            class="annot"
            :class="{'annot-selected':a.selected }"
            @click="select(a)"
            :key="a.id"
          >{{a.type}}</div>
        </div>
        <div class="note" v-if="i">
          <textarea v-model="i.note"></textarea>
        </div>
      </div>
      <div id="container"></div>
    </div>
    <context-menu id="context-menu" ref="ctxMenu">
      <li @click="doSomething()">アラートイベント</li>
      <li class="disabled">disabled</li>
      <li>option 3</li>
    </context-menu>
  </div>
</template>

<script lang="ts">
import AutoSaver from "components/auto-saver.vue";
import { AnnotationObject, TargetImage } from "domain/targetimage";
import Konva from "konva";
import { MyVue } from "types";
import { guid } from "utils/misc";
import Vue from "vue";
import Component from "vue-class-component";

import { Watch, Prop, Ref } from "vue-property-decorator";
import { Point } from "konva/types/Util";
import { debounce } from "throttle-debounce";
import { imageUrl } from "service/image-service";
import { clone } from "utils/objects";
import { ImageType } from "domain/imagetype";

import contextMenu from "vue-context-menu";

const COLORS = [
  "red",
  "yellow",
  "green",
  "blue",
  "cyan",
  "pink",
  "orange",
  "purple",
  "brown"
];

type Mode = "draw" | "select" | "cut" | "fit" | "all" | "set" | "erase" | "";

export default interface RectEditor extends MyVue {}
@Component({
  components: { AutoSaver, contextMenu }
})
export default class RectEditor extends Vue {
  @Prop()
  type: ImageType;

  getType(key): string {
    return this.type.types.find(t => t.id === key).name;
  }

  get types() {
    if (this.type) return this.type.types.filter(t => t.id !== "1_overall");
    else return [];
  }

  get colors() {
    if (this.type)
      return this.type.types
        .filter(t => t.id !== "1_overall")
        .reduce((map, c, i) => {
          map[c.id] = COLORS[i % COLORS.length];
          return map;
        }, {});
    else return {};
  }

  _currentType = null;
  get currentType() {
    if (this.selected) {
      return this.selected.type;
    } else {
      return this._currentType;
    }
  }

  set currentType(t) {
    if (this.selected) {
      this.selected.setType(t);
      this.stage.batchDraw();
    } else {
      this._currentType = t;
    }
  }

  i: TargetImage = null;

  @Ref() ctxMenu: contextMenu;

  setImage(neu: TargetImage) {
    this.i = neu;
    this.init();

    this.filteEnabled = false;
    this.showImage(imageUrl(neu));
    this.threshold = neu.bwThreshold | 128;
    this.setRects(neu.annotations);
    let all = neu.annotations.find(at => at.name === "1_overall");
    if (all) {
      this.rectAll = new AnnotRect(all, this, true);
    } else {
      this.rectAll = new AnnotRect(
        {
          id: guid(),
          name: "1_overall",
          bndbox: {
            xmin: 100,
            ymin: 100,
            xmax: neu.size.width - 100,
            ymax: neu.size.height - 100
          }
        },
        this,
        true
      );
    }
    this.watchFunc(this.func);
    this.$nextTick(() => {
      this.autosaver.reset();
    });
  }

  private setRects(obs: AnnotationObject[]) {
    if (obs != null) {
      this.rectLayer.removeChildren();
      this.rects = obs
        .filter(at => at.name !== "1_overall")
        .map(at => new AnnotRect(at, this, false));
    }
  }

  @Ref("autosaver") autosaver: AutoSaver;

  stage: Konva.Stage;

  //ツールバー
  func: Mode = "select";

  @Watch("func")
  watchFunc(neu: string) {
    if (neu === "select") {
      this.rects.forEach(r => r.setDraggable(true));
    } else {
      this.rects.forEach(r => r.setDraggable(false));
      this.select(null);
    }
    if (neu === "all") {
      this.rectLayer.setAttrs({ visible: false });
      this.rectAll.select(true);
      this.rectAll.setFill(true);
    } else {
      this.rectLayer.setAttrs({ visible: true });
      this.rectAll.select(false);
      this.rectAll.setFill(false);
    }
    if (neu !== "cut") {
      this.cutLineLayer.showLine("none", 0, 0);
    }
    if (neu != "erase") {
      this.deleteArea.setAttrs({ visible: false });
    }
    if (neu != "select") this.tempSelectMode = "";
    document.body.style.cursor = "default";
    this.tempDrawMode = false;
    this.stage.batchDraw();
  }

  fullFit() {
    document.body.style.cursor = "wait";
    this.rects.forEach(r => {
      r.autoFit();
      r.setDragging(false);
    });
    document.body.style.cursor = "auto";
  }

  //線
  lineLayer: HelpLineLayer;

  //裁断
  cutLineLayer: CutLineLayer;
  cutType: "vertical" | "horizontal" = "vertical";
  autoFit: boolean = true;
  fitTop: boolean = true;
  fitBtm: boolean = true;
  fitLeft: boolean = true;
  fitRight: boolean = true;
  fitDefensive: boolean = false;

  //全体
  overallLayer: Konva.Layer;
  rectAll: AnnotRect = null;

  //矩形
  rects: AnnotRect[] = null;
  rectLayer: Konva.Layer;
  tempRect: AnnotRect;
  selected: AnnotRect = null;
  tempDrawMode: boolean = false;

  //削除系

  deleteArea: Konva.Rect;

  getResult() {
    let result = clone(this.i);
    result.annotations = this.dorects.concat(this.rectAll.getAnnot());
    result.bwThreshold = this.threshold;
    return result;
  }

  get dorects(): AnnotationObject[] {
    if (!this.rects) return null;
    return this.rects.map(r => r.getAnnot());
  }

  set dorects(r: AnnotationObject[]) {
    this.setRects(r);
    this.rectLayer.batchDraw();
  }

  removeRect(r: AnnotRect) {
    if (r) {
      r.delete();
      this.rects.splice(this.rects.indexOf(r), 1);
    }
  }

  tempDraw() {
    this.func = "draw";
    this.tempDrawMode = true;
  }

  select(ar: AnnotRect) {
    if (ar) {
      this.func = "select";
      ar.select(true);
      this.rectLayer.batchDraw();
    } else {
      if (this.selected) this.selected.select(false);
      this.selected = null;
      this.rectLayer.batchDraw();
    }
  }

  //画像系
  imageLayer: Konva.Layer;
  baseImage: Konva.Image;
  image: HTMLImageElement;
  threshold: number = 128;
  filteEnabled: boolean = false;
  bwArray: boolean[][];
  tempSelectMode: Mode = "";

  setFilter() {
    if (this.baseImage) {
      if (this.filteEnabled) {
        this.baseImage.filters([this.bwFilter]);
      } else {
        this.bwArray = null;
        this.baseImage.filters([]);
      }
      this.baseImage.cache();
      this.stage.batchDraw();
    }
  }

  bwFilter(imageData: ImageData) {
    this.bwArray = new Array(imageData.width);
    let trueCount = 0;
    for (let x = 0; x < imageData.width; x++) {
      this.bwArray[x] = new Array(imageData.height);
      for (let y = 0; y < imageData.height; y++) {
        let r = imageData.data[y * (imageData.width * 4) + x * 4 + 0];
        let g = imageData.data[y * (imageData.width * 4) + x * 4 + 1];
        let b = imageData.data[y * (imageData.width * 4) + x * 4 + 2];
        let yy = 0.299 * r + 0.587 * g + 0.114 * b;
        if (yy > this.threshold) {
          imageData.data[y * (imageData.width * 4) + x * 4 + 0] = 256;
          imageData.data[y * (imageData.width * 4) + x * 4 + 1] = 256;
          imageData.data[y * (imageData.width * 4) + x * 4 + 2] = 256;
          this.bwArray[x][y] = false;
        } else {
          imageData.data[y * (imageData.width * 4) + x * 4 + 0] = 0;
          imageData.data[y * (imageData.width * 4) + x * 4 + 1] = 0;
          imageData.data[y * (imageData.width * 4) + x * 4 + 2] = 0;
          this.bwArray[x][y] = true;
          trueCount++;
        }
      }
    }
  }

  showImage(url: string) {
    if (this.baseImage) {
      this.baseImage.clearCache();
      this.baseImage.remove();
      this.baseImage.destroy();
    }
    this.image = new Image();
    this.image.onload = () => {
      this.baseImage = new Konva.Image({
        x: 0,
        y: 0,
        image: this.image,
        width: this.image.width,
        height: this.image.height
      });

      this.imageLayer.add(this.baseImage);
      this.imageLayer.batchDraw();
    };
    this.image.src = url;
  }

  get scaleBar() {
    return Math.ceil(this.scale * 100);
  }

  set scaleBar(s) {
    this.scale = s / 100;
  }

  private debouncedScale: () => void = debounce(250, this.setScale);
  private debouncedFilter: () => void = debounce(250, this.setFilter);

  scaleBy = 1.05;
  scale = 1;

  getRelativePointerPosition(): Point {
    var transform = this.stage.getAbsoluteTransform().copy();
    transform.invert();
    var pos = this.stage.getPointerPosition();
    pos = transform.point(pos);
    pos.x = Math.round(pos.x);
    pos.y = Math.round(pos.y);
    return pos;
  }
  setScale() {
    if (this.stage) {
      this.stage.scale({ x: this.scale, y: this.scale });
      this.stage.setAttrs({
        width: 1600 * this.scale,
        height: 1200 * this.scale
      });
    }
  }
  private init() {
    if (this.stage) {
      this.stage.destroy();
    }
    this.stage = new Konva.Stage({
      container: "container",
      width: 1600,
      height: 1200
    });
    //初期スケール
    this.setScale();

    this.stage.on("wheel", e => {
      if (e.evt.shiftKey) {
        e.evt.preventDefault();
        var oldScale = this.scale;
        this.scale =
          e.evt.deltaY > 0 ? oldScale * this.scaleBy : oldScale / this.scaleBy;
        this.stage.scale({ x: this.scale, y: this.scale });
        this.stage.setAttrs({
          width: 1600 * this.scale,
          height: 1200 * this.scale
        });
        this.stage.batchDraw();
      }
    });

    //画像
    this.imageLayer = new Konva.Layer();
    this.stage.add(this.imageLayer);

    //Overall
    this.overallLayer = new Konva.Layer();
    this.stage.add(this.overallLayer);

    //矩形
    this.rectLayer = new Konva.Layer();
    this.stage.add(this.rectLayer);

    this.stage.on("mousedown", e => {
      let pos = this.getRelativePointerPosition();
      if (this.tempRect == null && this.func === "draw") {
        if (this._currentType == null) this._currentType = this.types[0].id;
        this.tempRect = new AnnotRect(
          {
            id: guid(),
            name: this._currentType,
            bndbox: {
              xmin: pos.x,
              ymin: pos.y,
              xmax: pos.x,
              ymax: pos.y
            }
          },
          this
        );
        this.tempRect.setDragging(true);
        e.cancelBubble = true;
      }
      if (this.func == "erase") {
        this.deleteArea.setAttrs({
          x: pos.x,
          y: pos.y,
          width: 0,
          height: 0,
          visible: true
        });
        e.cancelBubble = true;
      }
    });
    this.stage.on("mousemove", e => {
      if (this.func === "cut") {
        let pos = this.getRelativePointerPosition();
        this.cutLineLayer.showLine(this.cutType, pos.x, pos.y);
      }
      if (this.tempRect != null) {
        let pos = this.getRelativePointerPosition();
        this.tempRect.width = pos.x - this.tempRect.x;
        this.tempRect.height = pos.y - this.tempRect.y;
        this.tempRect.update(true);
        e.cancelBubble = true;
      }
      if (this.func === "erase" && this.deleteArea.visible) {
        let pos = this.getRelativePointerPosition();
        this.deleteArea.setAttrs({
          width: pos.x - this.deleteArea.x(),
          height: pos.y - this.deleteArea.y()
        });
        this.stage.batchDraw();
      }
    });
    this.stage.on("mouseup", e => {
      if (this.tempRect != null) {
        if (this.tempRect.width < 5 && this.tempRect.height < 5) {
          this.tempRect.setDragging(false);
          this.tempRect.delete();
          this.tempRect = null;
        } else {
          if (this.autoFit)
            this.tempRect.autoFit(
              this.fitTop,
              this.fitBtm,
              this.fitLeft,
              this.fitRight
            );
          this.tempRect.setDragging(false);
          this.rects.push(this.tempRect);
          this.tempRect = null;
        }
        e.cancelBubble = true;
      }
      if (this.func === "erase" && this.deleteArea.visible) {
        let x1 = this.deleteArea.x();
        let x2 = this.deleteArea.x() + this.deleteArea.width();
        let y1 = this.deleteArea.y();
        let y2 = this.deleteArea.y() + this.deleteArea.height();
        if (x1 > x2) {
          let temp = x1;
          x1 = x2;
          x2 = temp;
        }
        if (y1 > y2) {
          let temp = y1;
          y1 = y2;
          y2 = temp;
        }

        let deletes = this.rects.filter(
          r => x1 < r.x && r.x + r.width < x2 && y1 < r.y && r.y + r.height < y2
        );
        deletes.forEach(r => this.removeRect(r));
        this.deleteArea.setAttrs({ visible: false });
        e.cancelBubble = true;
        this.stage.batchDraw();
      }
    });
    this.stage.on("click", e => {
      if (this.func === "cut") {
        let pos = this.getRelativePointerPosition();
        let delRects = [];
        this.rects.forEach(r => {
          if (r.cut(this.cutType, pos.x, pos.y)) {
            delRects.push(r);
          }
        });
        delRects.forEach(r => this.removeRect(r));
        this.rectLayer.batchDraw();
      }
    });

    //線
    this.lineLayer = new HelpLineLayer();
    this.stage.add(this.lineLayer.lineLayer);

    //Cut
    this.cutLineLayer = new CutLineLayer();
    this.stage.add(this.cutLineLayer.lineLayer);

    let deleteLayer = new Konva.Layer();
    this.stage.add(deleteLayer);
    this.deleteArea = new Konva.Rect({
      x: 0,
      y: 0,
      width: 0,
      height: 0,
      visible: false,
      draggable: false,
      stroke: "red",
      strokeWidth: 1
    });
    deleteLayer.add(this.deleteArea);

    this.stage.batchDraw();
  }

  mounted() {
    // キーボード
    window.addEventListener("keydown", (e: KeyboardEvent) => {
      if (e.key === "Delete") {
        if (this.func === "select" && this.selected != null) {
          this.removeRect(this.selected);
          this.selected = null;
        }
        e.preventDefault();
        this.rectLayer.batchDraw();
      }
      if (!e.ctrlKey && e.key === "z") {
        this.func = "draw";
      } else if (e.key === "x") {
        this.func = "select";
      } else if (e.key === "c") {
        if (this.func === "cut") {
          if (this.cutType == "vertical") this.cutType = "horizontal";
          else this.cutType = "vertical";
        } else {
          this.func = "cut";
        }
      } else if (e.key === "v") {
        if (this.func === "fit") {
          this.fullFit();
        } else {
          this.func = "fit";
        }
      } else if (e.key === "b") {
        this.func = "set";
      } else if (e.key === "n") {
        this.filteEnabled = !this.filteEnabled;
        this.setFilter();
      }

      if (this.selected != null) {
        let t = this.types.find(t => t.id.startsWith(e.key));
        if (t) {
          this.selected.setType(t.id);
          this.stage.batchDraw();
          e.preventDefault();
        }
      }

      if (e.key === "Control") {
        if (this.func !== "select" && this.tempRect == null) {
          e.preventDefault();
          this.tempSelectMode = this.func;
          this.func = "select";
        }
      }
    });
    window.addEventListener("keyup", (e: KeyboardEvent) => {
      if (e.key === "Control") {
        if (this.tempSelectMode != "") {
          this.func = this.tempSelectMode;
          this.tempSelectMode = "";
        }
      }
    });
  }
}

class CutLineLayer {
  showLine(type: "horizontal" | "vertical" | "none", x, y) {
    if (this.lineLayer) {
      let h = 10000;
      let w = 10000;
      if (type !== "none") {
        this.lineLayer.setAttrs({ visible: true });
        if (type === "horizontal") {
          this.line.setAttrs({
            points: [0, y + 0.5, w, y + 0.5]
          });
        } else {
          this.line.setAttrs({
            points: [x + 0.5, 0, x + 0.5, h]
          });
        }
      } else {
        this.lineLayer.setAttrs({ visible: false });
      }
      this.lineLayer.batchDraw();
    }
  }
  lineLayer: Konva.Layer;
  line: Konva.Line;
  constructor() {
    this.lineLayer = new Konva.Layer({ visible: false });
    var ctx = this.lineLayer.getContext()._context;
    ctx.imageSmoothingEnabled = false;
    this.line = new Konva.Line({
      points: [0, 0, 0, 0],
      stroke: "red",
      strokeWidth: 1
    });
    this.lineLayer.add(this.line);
  }
}

class HelpLineLayer {
  showLine(visible: boolean, x?, y?, width?, height?) {
    if (this.lineLayer) {
      this.lineLayer.setAttrs({ visible });
      let h = 10000;
      let w = 10000;
      if (visible) {
        this.lineL.setAttrs({ points: [x + 0.5, 0, x + 0.5, h] });
        this.lineR.setAttrs({
          points: [x + width + 0.5, 0, x + width + 0.5, h]
        });
        this.lineT.setAttrs({ points: [0, y + 0.5, w, y + 0.5] });
        this.lineB.setAttrs({
          points: [0, y + height + 0.5, w, y + height + 0.5]
        });
        this.lineLayer.batchDraw();
      }
    }
  }
  lineLayer: Konva.Layer;
  lineL: Konva.Line;
  lineR: Konva.Line;
  lineT: Konva.Line;
  lineB: Konva.Line;
  constructor() {
    this.lineLayer = new Konva.Layer({ visible: false });
    var ctx = this.lineLayer.getContext()._context;
    ctx.imageSmoothingEnabled = false;
    this.lineL = this.createLine();
    this.lineLayer.add(this.lineL);
    this.lineR = this.createLine();
    this.lineLayer.add(this.lineR);
    this.lineT = this.createLine();
    this.lineLayer.add(this.lineT);
    this.lineB = this.createLine();
    this.lineLayer.add(this.lineB);
  }
  createLine() {
    return new Konva.Line({
      points: [0, 0, 0, 0],
      stroke: "red",
      strokeWidth: 1
    });
  }
}

const BASE_OPACITY = 0.2;
/**
 * アノテーション付き矩形
 *
 */
class AnnotRect {
  x: number;
  y: number;
  width: number;
  height: number;
  type: string;
  id: string;
  rectBorder;
  constructor(
    ao: AnnotationObject,
    private editor: RectEditor,
    private overall?: boolean
  ) {
    this.x = Math.min(ao.bndbox.xmin, ao.bndbox.xmax);
    this.y = Math.min(ao.bndbox.ymin, ao.bndbox.ymax);
    this.width = Math.abs(ao.bndbox.xmax - ao.bndbox.xmin);
    this.height = Math.abs(ao.bndbox.ymax - ao.bndbox.ymin);
    this.type = ao.name;
    this.id = ao.id;

    //Main Rect
    this.rectFill = new Konva.Rect({
      id: ao.id,
      x: this.x,
      y: this.y,
      width: this.width,
      height: this.height,
      visible: true,
      opacity: BASE_OPACITY,
      fill: editor.colors[this.type],
      draggable: false,
      stroke: "black",
      strokeWidth: 1
    });
    // this.rectFill.on("wheel", e => {
    //   if (this.isSelectable && !this.overall) {
    //     let d = e.evt.deltaX > 0 ? +1 : -1;
    //     let idx = this.editor.types.findIndex(t => t.id === this.type) + d;
    //     if (idx >= this.editor.types.length) idx = 0;
    //     if (idx < 0) idx = this.editor.types.length - 1;
    //     this.type = this.editor.types[idx].id;
    //     e.evt.preventDefault();
    //     this.setDragging(false);
    //   }
    // });
    this.rectFill.on("click", e => {
      if (this.isSelectable) {
        this.select(true);
      } else if (this.editor.func === "fit") {
        this.autoFit(
          this.editor.fitTop,
          this.editor.fitBtm,
          this.editor.fitLeft,
          this.editor.fitRight
        );
        this.setDragging(false);
      } else if (this.editor.func === "set") {
        this.setType(this.editor._currentType);
        this.setDragging(false);
      }
    });
    this.rectFill.on("mouseover", e => {
      if (this.isSelectable) {
        document.body.style.cursor = "grab";
      } else if (this.editor.func === "fit") {
        document.body.style.cursor = "grab";
      }
    });
    this.rectFill.on("mouseout", e => {
      if (this.isSelectable || this.editor.func === "fit") {
        document.body.style.cursor = "default";
      }
    });
    this.rectFill.on("dragstart", e => {
      if (this.isSelectable) {
        this.select(true);
        this.setDragging(true);
      }
      e.cancelBubble = true;
    });
    this.rectFill.on("dragmove", e => {
      if (this.isSelectable) {
        this.x = Math.round(this.rectFill.x());
        this.y = Math.round(this.rectFill.y());
        this.width = Math.round(this.rectFill.width());
        this.height = Math.round(this.rectFill.height());
        this.update(true);
      }
    });
    this.rectFill.on("dragend", e => {
      this.setDragging(false);
    });
    if (this.overall) {
      this.editor.overallLayer.add(this.rectFill);
      this.rectFill.setAttrs({ stroke: "blue", strokeWidth: 4 });
      this.setFill(false);
    } else {
      this.editor.rectLayer.add(this.rectFill);
    }

    //サイズ変更丸
    this.addCircle("nw-resize", (x, y, tlx, tly, brx, bry) => {
      if (x < brx) {
        this.x = x;
        this.width = brx - x;
      }
      if (y < bry) {
        this.y = y;
        this.height = bry - y;
      }
      if (this.editor.autoFit) this.autoFit(true, false, true, false);
    });
    this.addCircle("n-resize", (x, y, tlx, tly, brx, bry) => {
      if (y < bry) {
        this.y = y;
        this.height = bry - y;
      }
      if (this.editor.autoFit) this.autoFit(true, false, false, false);
    });
    this.addCircle("ne-resize", (x, y, tlx, tly, brx, bry) => {
      if (x > tlx) {
        this.width = x - tlx;
      }
      if (y < bry) {
        this.y = y;
        this.height = bry - y;
      }
    });
    this.addCircle("w-resize", (x, y, tlx, tly, brx, bry) => {
      if (x < brx) {
        this.x = x;
        this.width = brx - x;
      }
    });
    this.addCircle("e-resize", (x, y, tlx, tly, brx, bry) => {
      if (x > tlx) {
        this.width = x - tlx;
      }
    });
    this.addCircle("sw-resize", (x, y, tlx, tly, brx, bry) => {
      if (x < brx) {
        this.x = x;
        this.width = brx - x;
      }
      if (y > tly) {
        this.height = y - tly;
      }
    });
    this.addCircle("s-resize", (x, y, tlx, tly, brx, bry) => {
      if (y > tly) {
        this.height = y - tly;
      }
    });
    this.addCircle("se-resize", (x, y, tlx, tly, brx, bry) => {
      if (x > tlx) {
        this.width = x - tlx;
      }
      if (y > tly) {
        this.height = y - tly;
      }
    });
    this.update(false);
  }

  get isSelectable() {
    return (
      (this.editor.func === "select" && !this.overall) ||
      (this.editor.func === "all" && this.overall)
    );
  }

  get selected() {
    return this === this.editor.selected;
  }

  select(flag: boolean) {
    if (flag) {
      if (this.editor.selected) {
        this.editor.selected.select(false);
      }
      this.rectFill.moveToTop();
      this.circles.forEach(c => {
        c.moveToTop();
      });
      this.circleVisible = true;
      this.showCircle(true);
      this.editor.selected = this;
    } else {
      this.circleVisible = false;
      this.showCircle(false);
      this.editor.selected = null;
    }
  }

  setType(t: string) {
    this.type = t;
    this.rectFill.setAttr("fill", this.editor.colors[t]);
  }

  setFill(fillEnabled: boolean) {
    this.rectFill.setAttrs({ fillEnabled });
  }

  setDraggable(draggable: boolean) {
    this.rectFill.setAttrs({ draggable });
  }

  setDragging(flag: boolean) {
    if (flag) {
      this.rectFill.setAttrs({ visible: false });
      this.showCircle(false);
    } else {
      this.rectFill.setAttrs({
        visible: true,
        x: this.x,
        y: this.y,
        width: this.width,
        height: this.height
      });
      this.showCircle(this.circleVisible);
    }

    this.update(flag);
    if (this.overall) this.editor.overallLayer.batchDraw();
    else this.editor.rectLayer.batchDraw();
  }

  circleVisible: boolean = false;
  showCircle(visible: boolean) {
    this.circles.forEach(c => c.setAttrs({ visible }));
  }

  update(dragging: boolean) {
    let uc: number[][] = [
      [this.x, this.y],
      [this.x + this.width / 2, this.y],
      [this.x + this.width, this.y],
      [this.x, this.y + this.height / 2],
      [this.x + this.width, this.y + this.height / 2],
      [this.x, this.y + this.height],
      [this.x + this.width / 2, this.y + this.height],
      [this.x + this.width, this.y + this.height]
    ];
    for (let i = 0; i < 8; i++) {
      let xy: number[] = uc[i];
      this.circles[i].setAttrs({ x: xy[0], y: xy[1] });
    }
    //Line
    if (this.editor.lineLayer)
      this.editor.lineLayer.showLine(
        dragging,
        this.x,
        this.y,
        this.width,
        this.height
      );
  }

  addCircle(cursor: string, sizeChange: (x, y, tlx, tly, brx, bry) => void) {
    let circle = new Konva.Circle({
      radius: 5,
      stroke: "black",
      strokeWidth: 0.5,
      draggable: true,
      visible: false,
      fill: "white"
    });
    circle.on("mouseover", e => {
      document.body.style.cursor = cursor;
      e.cancelBubble = true;
    });
    circle.on("mouseout", e => {
      document.body.style.cursor = "default";
      e.cancelBubble = true;
    });
    circle.on("dragstart", e => {
      this.setDragging(true);
      e.cancelBubble = true;
    });
    circle.on("dragmove", e => {
      let p = this.editor.getRelativePointerPosition();
      sizeChange(
        p.x,
        p.y,
        this.rectFill.x(),
        this.rectFill.y(),
        this.rectFill.x() + this.rectFill.width(),
        this.rectFill.y() + this.rectFill.height()
      );
      this.update(true);
      e.cancelBubble = true;
    });
    circle.on("dragend", e => {
      this.rectFill.setAttrs({
        x: this.x,
        y: this.y,
        width: this.width,
        height: this.height
      });
      this.setDragging(false);
      e.cancelBubble = true;
    });
    if (this.overall) this.editor.overallLayer.add(circle);
    else this.editor.rectLayer.add(circle);
    this.circles.push(circle);
  }

  delete() {
    this.rectFill.remove();
    this.circles.forEach(c => c.remove());
  }

  autoFit(top = true, bottom = true, left = true, right = true) {
    if (this.editor.bwArray) {
      const maxHeight = this.editor.bwArray[0].length;
      const maxWidth = this.editor.bwArray.length;
      const min = 20;
      //上側
      if (top) {
        //上辺上が全部白い限り、上辺を1ずつ下に移動する
        inf: while (this.height > min) {
          for (let x = this.x; x <= this.x + this.width; x++) {
            if (this.editor.bwArray[x][this.y]) break inf;
          }
          this.y++;
          this.height--;
        }
        //黒を見つける限り、上に移動する。
        while (this.height < maxHeight) {
          let count = 0;
          for (let x = this.x; x <= this.x + this.width; x++) {
            if (this.editor.bwArray[x][this.y - 1]) count++;
          }
          if (count > 0) {
            this.y--;
            this.height++;
          } else break;
        }
      }
      //下側
      if (bottom) {
        inf: while (this.height > min) {
          for (let x = this.x; x <= this.x + this.width; x++) {
            if (this.editor.bwArray[x][this.y + this.height - 1]) break inf;
          }
          this.height--;
        }
        while (this.height < maxHeight) {
          let count = 0;
          for (let x = this.x; x <= this.x + this.width; x++) {
            if (this.editor.bwArray[x][this.y + this.height]) count++;
          }
          if (count > 0) {
            this.height++;
          } else break;
        }
      }
      //左側
      if (left) {
        inf: while (this.width > min) {
          for (let y = this.y; y <= this.y + this.height; y++) {
            if (this.editor.bwArray[this.x][y]) break inf;
          }
          this.x++;
          this.width--;
        }
        while (this.width < maxWidth) {
          let count = 0;
          for (let y = this.y; y <= this.y + this.height; y++) {
            if (this.editor.bwArray[this.x - 1][y]) count++;
          }
          if (count > 0) {
            this.x--;
            this.width++;
          } else break;
        }
      }
      //右側
      if (right) {
        inf: while (this.width > min) {
          for (let y = this.y; y <= this.y + this.height; y++) {
            if (this.editor.bwArray[this.x + this.width - 1][y]) break inf;
          }
          this.width--;
        }
        while (this.width < maxWidth) {
          let count = 0;
          for (let y = this.y; y <= this.y + this.height; y++) {
            if (this.editor.bwArray[this.x + this.width + 1][y]) count++;
          }
          if (count > 0) {
            this.width++;
          } else break;
        }
      }
    }
  }

  cut(type: "horizontal" | "vertical", x: number, y: number): boolean {
    if (
      this.x < x &&
      x < this.x + this.width &&
      this.y < y &&
      y < this.y + this.height
    ) {
      if (type === "horizontal") {
        let upper = new AnnotRect(
          {
            id: guid(),
            name: this.type,
            bndbox: {
              xmin: this.x,
              xmax: this.x + this.width,
              ymin: this.y,
              ymax: y - 1
            }
          },
          this.editor
        );
        let lower = new AnnotRect(
          {
            id: guid(),
            name: this.type,
            bndbox: {
              xmin: this.x,
              xmax: this.x + this.width,
              ymin: y + 1,
              ymax: this.y + this.height
            }
          },
          this.editor
        );
        this.editor.rects.push(upper, lower);
        if (this.editor.autoFit) {
          upper.autoFit();
          upper.setDragging(false);
          lower.autoFit();
          lower.setDragging(false);
        }
        return true;
      } else {
        let left = new AnnotRect(
          {
            id: guid(),
            name: this.type,
            bndbox: {
              ymin: this.y,
              ymax: this.y + this.height,
              xmin: this.x,
              xmax: x - 1
            }
          },
          this.editor
        );
        let right = new AnnotRect(
          {
            id: guid(),
            name: this.type,
            bndbox: {
              ymin: this.y,
              ymax: this.y + this.height,
              xmin: x + 1,
              xmax: this.x + this.width
            }
          },
          this.editor
        );
        this.editor.rects.push(left, right);
        if (this.editor.autoFit) {
          left.autoFit();
          left.setDragging(false);
          right.autoFit();
          right.setDragging(false);
        }
        return true;
      }
    }
    return false;
  }

  getAnnot() {
    return {
      id: this.id,
      name: this.type,
      bndbox: {
        xmin: this.x,
        ymin: this.y,
        xmax: this.x + this.width,
        ymax: this.y + this.height
      }
    };
  }

  circles: Konva.Circle[] = [];
  // rectLine: Konva.Rect;
  rectFill: Konva.Rect;
}
</script>