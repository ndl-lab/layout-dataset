let S4 = function() {
  return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
};

export function guid(): string {
  return S4() + S4();
}

export function split(str: string): string[] {
  const result = [];
  let semiFlag = false;
  let substr = "";
  for (var i = 0; i < str.length; i++) {
    if (str[i] === '"') {
      if (semiFlag) {
        result.push('"' + substr + '"');
        substr = "";
        semiFlag = false;
      } else {
        semiFlag = true;
      }
    } else if (!semiFlag && /[\s　]/.exec(str.substr(i, 1))) {
      if (substr.length > 0) {
        result.push(substr);
        substr = "";
      }
    } else {
      substr += str[i];
    }
  }
  if (substr.length > 0) {
    result.push(substr);
  }
  return result;
}

export function validateEmail(mail: string) {
  return /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(mail.trim());
}

export function containUrl(content: string): boolean {
  if (!content) return false;
  return /.+h*ttps*:\/\//.test(content);
}

export function abbText(str: string, len: number) {
  if (!str) return "";
  if (str.length < len) return str;
  return str.substring(0, len) + "...";
}

export function hashCode(str: string): number {
  let hash = 0;
  for (var i = 0; i < str.length; i++) {
    hash = hash * 31 + str.charCodeAt(i);
    hash = hash | 0; //符号付き32bit整数にする。
  }
  return hash;
}

export type DeviceType = "mobile" | "tablet" | "desktop";

export function getDevice(): DeviceType {
  let ua = navigator.userAgent;
  if (
    ua.indexOf("iPhone") > 0 ||
    ua.indexOf("iPod") > 0 ||
    (ua.indexOf("Android") > 0 && ua.indexOf("Mobile") > 0)
  ) {
    return "mobile";
  } else if (ua.indexOf("iPad") > 0 || ua.indexOf("Android") > 0) {
    return "tablet";
  } else {
    return "desktop";
  }
}

export function findMatch(s: string, regxp: RegExp, group = 0): string {
  return regxp.exec(s)[group];
}
