import * as URL from "utils/url";

export interface Range {
  from: string;
  to: string;
}

export interface GeoDistance {
  lat: number;
  lon: number;
  distance: string;
}

export class SearchQuery {
  from?: number;
  size?: number;
  keyword?: string[];
  keywordOR?: boolean;
  query?: { [key: string]: string[] };
  range?: { [key: string]: Range[] };
  image?: string[];
  geoDistance?: { [key: string]: GeoDistance[] };
  exists?: string[];
  filter?: { [key: string]: string[] };
  facet?: { [key: string]: string[] };
  sort?: string[];
}

function parseBool(values: string[]): boolean {
  if (values && values.length > 0) {
    return values[0] === "true";
  }
  return false;
}

export function fromQueryString(query: string): SearchQuery {
  let q = new SearchQuery();
  let map = URL.getQueryMap(query);

  if (map["from"]) q.from = Number(map["from"]);
  if (!Number.isInteger(q.from) || q.from < 0) q.from = 0;

  if (map["size"]) q.size = Number(map["size"]);
  if (!Number.isInteger(q.size) || q.size < 0) q.size = 10;

  if (map["sort"]) {
    q.sort = map["sort"];
  }

  if (map["keyword"]) {
    q.keyword = map["keyword"];
  }
  if (map["image"]) {
    q.image = map["image"];
  }

  if (map["keywordOr"]) {
    q.keywordOR = parseBool(map["keywordOr"]);
  }

  q.exists = map["exists"];

  q.facet = {};
  q.query = {};
  q.filter = {};
  q.range = {};
  Object.keys(map).forEach(key => {
    if (key.startsWith("q-")) {
      q.query[key.substring(2)] = map[key];
    }
    if (key.startsWith("f-")) {
      q.filter[key.substring(2)] = map[key];
    }
    if (key.startsWith("r-")) {
      q.range[key.substring(2)] = map[key].map(v => {
        let spl = v.split(/,/, 2);
        if (spl.length >= 2) {
          return { from: spl[0], to: spl[1] };
        }
      });
    }
    if (key.startsWith("fc-")) {
      q.facet[key.substring(3)] = map[key];
    }
  });
  return q;
}

export function toSearchQueryString(q: SearchQuery) {
  return URL.toQueryString(toSeqrchQueryUrlMap(q));
}

function filter(input: string[]): string[] {
  return input.filter(s => s != null && s !== "");
}

export function toSeqrchQueryUrlMap(sq: SearchQuery) {
  let q: { [key: string]: any[] } = {};
  if (sq.from != null) q["from"] = [sq.from];
  if (sq.size && sq.size !== 20) q["size"] = [sq.size];
  if (sq.sort) q["sort"] = filter(sq.sort);
  if (sq.keyword) q["keyword"] = filter(sq.keyword);
  if (sq.image) q["image"] = filter(sq.image);
  if (sq.keywordOR) q["keywordOr"] = [true];
  if (sq.exists) q["exists"] = filter(sq.exists);
  if (sq.query) {
    Object.keys(sq.query).forEach(key => {
      let val: string[] = sq.query[key];
      if (val) q["q-" + encodeURIComponent(key)] = filter(val);
    });
  }
  if (sq.range) {
    Object.keys(sq.range).forEach(key => {
      let val = sq.range[key];
      if (val)
        q["r-" + encodeURIComponent(key)] = val.map(
          s =>
            encodeURIComponent(s.from || "") +
            "," +
            encodeURIComponent(s.to || "")
        );
    });
  }
  if (sq.geoDistance) {
    Object.keys(sq.geoDistance).forEach(key => {
      let val = sq.geoDistance[key];
      if (val)
        q["g-" + encodeURIComponent(key)] = val.map(
          s => s.lat + "," + s.lon + "," + s.distance
        );
    });
  }
  if (sq.filter) {
    Object.keys(sq.filter).forEach(key => {
      let val: string[] = sq.filter[key];
      if (val) q["f-" + encodeURIComponent(key)] = filter(val);
    });
  }

  if (sq.facet) {
    Object.keys(sq.facet).forEach(key => {
      let val: string[] = sq.facet[key];
      if (val) q["fc-" + encodeURIComponent(key)] = filter(val);
    });
  }
  return q;
}

export interface SearchResult<T> {
  list: T[];
  hit: number;
  from: number;
  facets: EsFacet[];
}

export interface EsFacet {
  field: string;
  counts: { [key: string]: number };
}

export type APICallType = "P" | "M" | "O";
