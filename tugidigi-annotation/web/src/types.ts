import { AxiosPromise } from "axios";
import * as Config from "config";
import { AppState, state } from "model/state";
import { clone, deepFind, equals, isEmpty } from "utils/objects";
import Vue, { VNodeDirective } from "vue";
import Component from "vue-class-component";

export interface LangString {
  ja?: string;
  en?: string;
}

export interface MyVue {
  $state: AppState;

  $jump(url: string, tgtBlank?: boolean): void;

  $abb(str: string, len: number): string;

  $confirm(msg: string): Promise<any>;
  $notifySuccess(msg: string, once?: boolean);
  $notifyError(msg: string, once?: boolean);
  $notifyInfo(
    msg: string,
    actionText: string,
    onAction: () => void,
    once?: boolean
  );
  $clearNotify(): void;

  $isEmpty(v: any): boolean;
  $join(arr: string[] | string): string;
  $lengthInSec(v: number): number;
  $scrollToTop();
  $scrollToSearchResult();
  $disableScroll();
  $enableScroll();

  $setLoading(flag: boolean);
  $isLoading(): boolean;
  $loadingExecutor<T>(): LoadingExecutor<T>;

  $url(path: string): string;
  $fullUrl(path: string): string;
  $arrayremove(array: any[], value: any): void;
  $setProperties(tgt: object, to: object): void;

  $hasAdminFor(data): boolean;
  $hasEditFor(data): boolean;

  $num(n: number): string;
  $date(date: number): string;
  $dateTime(date: number): string;
}

//アプリケーションで共通の状態

class LoadingExecutor<T> {
  constructor(private g: GlobalMixin) {}

  /**
   * Loadingを消したあと、成功の場合にはAPI結果のデータを返し、失敗だった場合にはエラーコードを返す
   * */
  execute(promise: AxiosPromise<T>): Promise<T> {
    return promise
      .then(r => {
        this.g.$setLoading(false);
        return r.data;
      })
      .catch(r => {
        this.g.$setLoading(false);
        return Promise.reject(r.response.data);
      });
  }

  /**
   * Loadingを消したあと、通知を行う。成功の場合にはAPI結果のデータを返し、失敗だった場合にはエラーコードを返す
   * */
  executeAndNotify(successMsg: string, promise: AxiosPromise<T>): Promise<T> {
    return promise
      .then(r => {
        this.g.$notifySuccess(successMsg);
        this.g.$setLoading(false);
        return r.data;
      })
      .catch(r => {
        let code = deepFind(r, "response.data.title");
        this.g.$notifyError("エラー");
        this.g.$setLoading(false);
        return Promise.reject(code);
      });
  }
}

//言語とコード

let nnnlist: any[] = [];
@Component({})
class GlobalMixin extends Vue {
  get $state(): AppState {
    return state;
  }

  $url(path: string) {
    return Config.BASE_PATH + path;
  }

  $abb(str: string, len: number): string {
    if (!str) return "";
    if (str.length < len) return str;
    return str.substring(0, len) + "...";
  }

  $join(arr: string[] | string): string {
    if (arr && Array.isArray(arr)) return arr.join(", ");
    else return <string>arr;
  }

  $isEmpty(v: any): boolean {
    return isEmpty(v);
  }

  $lengthInSec(len: number): number {
    return len / (5000000 / 166833);
  }

  $secInMin(sec: number) {
    let m = Math.floor(sec / 60);
    let s = Math.floor(sec % 60);

    let mDisplay = m > 0 ? m + "分" : "";
    let sDisplay = s > 0 ? s + "秒" : "";
    return mDisplay + sDisplay;
  }

  $confirm(msg: string): Promise<any> {
    return new Promise((resolve, reject) => {
      this.$buefy.dialog.confirm({
        message: msg,
        confirmText: "はい",
        cancelText: "いいえ",
        onConfirm: resolve,
        onCancel: reject
      });
    });
  }

  $notifySuccess(msg: string, once: boolean = false) {
    if (once && nnnlist.some(n => n.isActive && n.message === msg)) return;
    nnnlist.push(
      this.$buefy.snackbar.open({
        duration: 6000,
        message: msg,
        type: "is-success",
        position: "is-bottom",
        // indefinite: true,
        actionText: " ",
        queue: false
      })
    );
  }

  $notifyInfo(
    msg: string,
    actionText: string,
    onAction: () => void,
    once: boolean = false
  ) {
    if (once && nnnlist.some(n => n.isActive && n.message === msg)) return;
    nnnlist.push(
      this.$buefy.snackbar.open({
        message: msg,
        type: "is-info",
        position: "is-bottom",
        duration: 6000,
        actionText: actionText,
        onAction: onAction
      })
    );
  }

  $notifyError(msg: string, once: boolean = false) {
    if (once && nnnlist.some(n => n.isActive && n.message === msg)) return;
    nnnlist.push(
      this.$buefy.snackbar.open({
        message: msg,
        type: "is-danger",
        position: "is-bottom",
        indefinite: true,
        actionText: "close",
        queue: false
      })
    );
  }

  $clearNotify() {
    nnnlist.filter(n => n.isActive).forEach(n => n.close());
    nnnlist = [];
  }

  $setProperties(tgt: object, to: object) {
    let toKeys = Object.keys(to);
    Object.keys(tgt)
      .filter(key => !toKeys.includes(key))
      .forEach(delKey => {
        this.$delete(tgt, delKey);
      });
    toKeys.forEach(key => {
      this.$set(tgt, key, clone(to[key]));
    });
  }

  $loadingExecutor<T>(): LoadingExecutor<T> {
    this.$setLoading(true);
    return new LoadingExecutor<T>(this);
  }

  $arrayremove(array: any[], value: any) {
    let index = array.indexOf(value);
    if (index >= 0) this.$delete(array, index);
  }

  $setLoading(flag: boolean) {
    setLoading(flag);
  }

  $isLoading(): boolean {
    return isLoading();
  }

  $scrollToTop() {
    window.scrollTo(0, 0);
  }

  $date(date: number): string {
    if (!date) return "";
    let dateObject = new Date(date);
    return (
      dateObject.getFullYear() +
      "/" +
      this.zeroPadding(dateObject.getMonth() + 1, 2) +
      "/" +
      this.zeroPadding(dateObject.getDate(), 2)
    );
  }

  $jump(url: string, tgtBlank: boolean = false) {
    if (tgtBlank) {
      window.open(url, "_blank");
    } else window.location.href = url;
  }

  $errorsrc(url: string, ev) {
    ev.srcElement.src = url;
  }

  $num(n: number): string {
    if (n != null) return n.toLocaleString();
    return "";
  }

  $setTitle(title: string) {
    if (title) document.title = title + " - " + Config.SERVICE_NAME;
    else document.title = Config.SERVICE_NAME;
  }

  $disableScroll() {
    disableScroll();
  }

  $enableScroll() {
    enableScroll();
  }

  $dateTime(date: number): string {
    if (!date) return "";
    let dateObject = new Date(date);
    return (
      dateObject.getFullYear() +
      "/" +
      this.zeroPadding(dateObject.getMonth() + 1, 2) +
      "/" +
      this.zeroPadding(dateObject.getDate(), 2) +
      " " +
      this.zeroPadding(dateObject.getHours(), 2) +
      ":" +
      this.zeroPadding(dateObject.getMinutes(), 2) +
      ":" +
      this.zeroPadding(dateObject.getSeconds(), 2)
    );
  }

  beforeRouteEnter(to, from, next) {
    next(vm => {
      (<any>vm).setTitle();
    });
  }

  zeroPadding(num, length) {
    return ("0000000000" + num).slice(-length);
  }
}

export function disableScroll() {
  console.info("disable scroll");
  document.documentElement.classList.add("is-clipped");
}

export function enableScroll() {
  document.documentElement.classList.remove("is-clipped");
}

export function getFullPath(path: string) {
  return Config.BASE_PATH + path;
}

export function setLoading(flag: boolean) {
  let loadingElement = document.getElementsByClassName("loading-blocker").item(0);
  if (loadingElement) {
    if (flag) {
      loadingElement.classList.add("is-loading");
      window.setTimeout(() => setLoading(false), 10 * 1000);
    } else loadingElement.classList.remove("is-loading");
  }
}

export function isLoading(): boolean {
  let loadingElement = document.getElementsByClassName("loading-blocker").item(0);
  if (loadingElement) {
    loadingElement.classList.contains("is-loading");
  }
  return false;
}

Vue.mixin(GlobalMixin);