export function equals(o1: any, o2: any) {
  return JSON.stringify(o1) === JSON.stringify(o2);
}

// export function equalsIgnoreNull(o1: any, o2: any) {
//     const e1 = normalizeEmpty(o1), e2 = normalizeEmpty(o2);
//     return equals(e1, e2);
// }

export function equalsIgnoreNull(o1: any, o2: any): boolean {
  if (o1 == null || o2 == null) return false;
  if (o1 instanceof Date && o2 instanceof Date) {
    return o1.toDateString() === o2.toDateString();
  } else if (isEmpty(o1) && isEmpty(o2)) return true;
  else if (typeof o1 === "object" && typeof o2 === "object") {
    let o1keys = Object.keys(o1),
      o2keys = Object.keys(o2);
    // TODO join &unique
    return (o1keys.length > o2keys.length ? o1keys : o2keys).every(k =>
      equalsIgnoreNull(o1[k], o2[k])
    );
  } else if (Array.isArray(o1) && Array.isArray(o2)) {
    let len = Math.max(o1.length, o2.length);
    for (let i = 0; i < len; i++) {
      if (!equalsIgnoreNull(o1[i], o2[i])) return false;
    }
    return true;
  } else {
    return o1 === o2;
  }
}

export function isEmpty(v: any): boolean {
  if (v == null) return true;
  if (v === "") return true;
  if (v instanceof Date) return false;
  if (typeof v === "object") {
    let keys = Object.keys(v);
    if (keys.length == 0) return true;
    if (keys.map(k => v[k]).every(ov => isEmpty(ov))) return true;
  } else if (Array.isArray(v)) {
    if (v.length === 0) return true;
    if (v.every(av => isEmpty(av))) return true;
  }
  return false;
}

export function isEmptyString(v: string): boolean {
  return !v || /^[\s\0]+$/.test(v);
}

export function normalizeEmpty(v: any) {
  if (isEmpty(v)) return null;
  if (typeof v === "object") {
    let o = {};
    Object.keys(v).forEach(k => {
      o[k] = normalizeEmpty(v[k]);
    });
    return o;
  } else if (Array.isArray(v)) {
    let a = [];
    v.forEach(av => a.push(normalizeEmpty(av)));
    return a;
  }
  return v;
}

export function deepFind(obj: any, path: string, def?: any): any {
  for (let p of path.split(".")) {
    if (obj[p]) {
      obj = obj[p];
    } else {
      return def;
    }
  }
  return obj;
}
export function deepFind2(obj: any, path: string, def: any = []): any[] {
  let values = [];
  _deepFind2(path.split("."), obj, values);
  if (values.length == 0) return def;
  return values;
}

export function deepFind3(obj: any, paths: string[]): any[] {
  let values = [];
  paths.forEach(path => {
    _deepFind2(path.split("."), obj, values);
  });
  return values;
}

function _deepFind2(paths: string[], obj: any, values: any[]) {
  if (obj == null) return;
  let sub = obj[paths[0]];
  if (sub) {
    if (paths.length == 1) {
      if (Array.isArray(sub)) {
        (<any[]>sub).forEach(v => {
          values.push(v);
        });
      } else {
        values.push(sub);
      }
    } else {
      if (Array.isArray(sub)) {
        (<any[]>sub).forEach(node =>
          _deepFind2(paths.slice(1, paths.length), node, values)
        );
      } else {
        _deepFind2(paths.slice(1, paths.length), sub, values);
      }
    }
  }
}

export function clone<T>(tgt: T): T {
  let cp: any;
  if (tgt === null) {
    cp = null;
  } else if (tgt instanceof Date) {
    cp = new Date(tgt.getTime());
  } else if (Array.isArray(tgt)) {
    cp = [];
    tgt.forEach((v, i, arr) => {
      cp.push(v);
    });
    cp = cp.map((n: any) => clone(n));
  } else if (typeof tgt === "object" && tgt !== {}) {
    cp = { ...(tgt as Object) };
    Object.keys(cp).forEach(k => {
      cp[k] = clone(cp[k]);
    });
  } else {
    cp = tgt;
  }
  return cp;
}

export function merge(from: any, to: any) {
  if (typeof from === "object" && typeof to === "object") {
    Object.keys(from).forEach(k => {
      let fromval = from[k];
      //            console.info("merge", k, fromval);
      if (fromval === null) {
        //do nothing
      } else if (fromval instanceof Date) {
        to[k] = clone(fromval);
      } else if (Array.isArray(fromval)) {
        to[k] = clone(fromval);
      } else if (typeof fromval === "object") {
        let toval = to[k];
        if (toval != null && typeof toval === "object") {
          //toもfromもobjectの場合、マージ
          toval = clone(toval);
          merge(fromval, toval);
          to[k] = toval;
        } else {
          //toがobjectでない場合はcloneで上書き
          to[k] = clone(fromval);
        }
      } else {
        to[k] = fromval;
      }
    });
  }
}

export function deletePropertiesExcept(
  object: Object,
  ...exceptFor: string[]
): void {
  Object.keys(object).forEach(k => {
    if (!exceptFor.includes(k)) {
      delete object[k];
    }
  });
}

export function removeFromArray(array: any[], toRemove: any) {
  let index = array.indexOf(toRemove);
  if (index >= 0) array.splice(index, 1);
}

export function addOrMoveToTop(array: any[], toAdd: any) {
  removeFromArray(array, toAdd);
  array.unshift(toAdd);
}

export function flatArray(array: any) {
  return array.reduce((acc, val) => acc.concat(val), []);
}

export function uniqueArray(array: any) {
  return array.filter((v, i) => array.indexOf(v) === i);
}

export function findOrDefault(array: any[], cond: (v) => boolean, def: any) {
  let val = array.find(cond);
  if (val) return val;
  else return def;
}
