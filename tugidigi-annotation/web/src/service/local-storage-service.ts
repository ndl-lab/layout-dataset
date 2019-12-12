export async function lsSet(key: string, obj: Object) {
  if (!key || obj === undefined) return;
  // console.info("save local storage", key, obj);
  try {
    localStorage.setItem(key, JSON.stringify(obj));
  } catch (ex) {
    console.error(ex);
  }
}

export function lsGet<T>(key: string, defaultValue?: T): T {
  let str: string = localStorage.getItem(key);
  try {
    if (str) return <T>JSON.parse(str);
  } catch (ex) {
    console.error(ex);
  }
  return defaultValue;
}

export function lsDelete(key: string) {
  localStorage.removeItem(key);
}

export async function ssSet(key: string, obj: Object) {
  if (!key || obj === undefined) return;
  // console.info("save session storage", key, obj);
  try {
    sessionStorage.setItem(key, JSON.stringify(obj));
  } catch (ex) {
    console.error(ex);
  }
}

export function ssGet<T>(key: string, defaultValue?: T): T {
  let str: string = sessionStorage.getItem(key);
  try {
    if (str) return <T>JSON.parse(str);
  } catch (ex) {
    console.error(ex);
  }
  return defaultValue;
}

export function ssDelete(key: string) {
  sessionStorage.removeItem(key);
}
