<template>
  <div class="auto-saver">
    <transition name="fade">
      <div v-if="saving" class="saving">Saving...</div>
    </transition>
    <button class="icon-button" @click="undo()" :disabled="cantUndo">
      <b-icon size="is-small" icon="undo" />
    </button>
    <button class="icon-button" @click="redo()" :disabled="cantRedo">
      <b-icon size="is-small" icon="redo" />
    </button>
    <!-- <div v-for="(i,ind) in stack" :style="{color:ind==index?'red':'black'}">{{i}}</div> -->
  </div>
</template>>

<style lang="scss" scoped>
.saving {
  position: fixed;
  right: 0;
  bottom: 0;
  padding: {
    bottom: 2px;
    left: 4px;
    right: 4px;
  }
  background: darkgray;
}
</style>

<script lang="ts">
import Vue from "vue";
import Component from "vue-class-component";
import { Prop, Watch } from "vue-property-decorator";
import { debounce } from "throttle-debounce";
import { clone, equals } from "../utils/objects";
@Component({})
export default class AutoSaver extends Vue {
  @Prop()
  value: any;

  @Prop()
  saver: (obj) => Promise<any>;

  @Prop({ default: 5 })
  maxSize: number;

  stack: any[] = [];
  index = 0;
  undoOrRedoInProgress: boolean = false;

  get canUndo() {
    return this.index > 0;
  }

  get cantUndo() {
    return !this.canUndo;
  }

  get canRedo() {
    return this.stack.length - 1 > this.index;
  }

  get cantRedo() {
    return !this.canRedo;
  }

  update(obj: any) {
    this.undoOrRedoInProgress = true;
    this.$emit("input", obj);
    setTimeout(() => {
      this.undoOrRedoInProgress = false;
    }, 200);
  }

  public redo() {
    if (this.canRedo && !this.undoOrRedoInProgress) {
      this.index++;
      this.update(clone(this.stack[this.index]));
    }
  }

  public undo() {
    if (this.canUndo && !this.undoOrRedoInProgress) {
      this.index--;
      this.update(clone(this.stack[this.index]));
    }
  }

  public reset() {
    this.stack = [clone(this.value)];
    this.index = 0;
  }

  public saving: boolean = false;

  private addUndo() {
    if (!equals(this.value, this.stack[this.index])) {
      this.saving = true;
      //Redo領域の削除
      if (this.stack.length > this.index) {
        this.stack.splice(this.index + 1);
      }
      this.stack.push(clone(this.value));
      this.index++;
      if (this.index > this.maxSize) {
        this.stack.splice(0, 1);
        this.index--;
      }

      if (this.saver) {
        this.saver(this.value).then(() => {
          this.saving = false;
        });
      } else {
        this.$nextTick(() => {
          this.saving = false;
        });
      }
    }
  }

  private debouncedAddUndo: () => void = debounce(500, this.addUndo);

  @Watch("value", { deep: true, immediate: true })
  watchValue(neu, old) {
    if (this.undoOrRedoInProgress) {
      return;
    }
    if (old == null) {
      this.reset();
    } else {
      this.debouncedAddUndo();
    }
  }

  mounted() {
    document.onkeydown = keyEvent => {
      const keyCode = keyEvent.keyCode;
      if (keyEvent.metaKey === true || keyEvent.ctrlKey === true) {
        if (keyCode === 89) {
          this.redo();
          keyEvent.preventDefault();
          return false;
        } else if (keyCode === 90) {
          if (keyEvent.shiftKey === true) {
            this.redo();
          } else {
            this.undo();
          }
          keyEvent.preventDefault();
          return false;
        }
      }
    };
  }
}
</script>