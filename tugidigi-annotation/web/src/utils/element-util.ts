import Component from "vue-class-component";
import Vue from "vue";
import { Watch } from "vue-property-decorator";

let originalStyle: { [id: string]: string } = {};

export function show(id: string) {
  let e: HTMLElement = document.getElementById(id);
  if (e) {
    if (originalStyle[id]) {
      originalStyle[id] = undefined;
      e.style.display = originalStyle[id];
    } else e.style.display = "default";
  }
}

export function hide(id: string) {
  let e: HTMLElement = document.getElementById(id);
  if (e) {
    originalStyle[id] = e.style.display;
    e.style.display = "none";
  }
}

import "./element-util.scss";

@Component({})
export class AccordionMixin extends Vue {
  show: boolean = true;

  mounted() {
    if (!this.show) {
      const wrapper = <HTMLElement>this.$refs["wrapper"];
      wrapper.style.height = "0";
      wrapper.style.opacity = "0";
    }
  }

  @Watch("show")
  toggle(n, o) {
    const body = <HTMLElement>this.$refs["body"];
    const wrapper = <HTMLElement>this.$refs["wrapper"];
    if (this.show) {
      //こちらはDelayは不要だが、閉じるときとタイミングを合わせるため一応Delay付与
      // body.style.display = "block";
      window.setTimeout(() => {
        wrapper.style.height = `${body.offsetHeight}px`;
        wrapper.style.opacity = "1";
        //アニメーション終了後に、heightを外さないと、Body内部で要素が追加されたときに切れる
        //transition完了後にheightを消去
        window.setTimeout(() => {
          wrapper.style.height = "";
        }, 100);
      }, 50);
    } else {
      wrapper.style.height = `${body.offsetHeight}px`;
      wrapper.style.opacity = "1";
      // body.style.display = "none";
      //delayを入れないと、高さ設定が効かないためアニメーションしない
      window.setTimeout(() => {
        wrapper.style.height = "0";
        wrapper.style.opacity = "0";
      }, 50);
    }
  }
}

export interface IAccordionMixin {
  show: boolean;
}
