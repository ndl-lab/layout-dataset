import * as Axios from "axios";
import * as Config from "config";
import { SearchQuery, SearchResult, toSearchQueryString } from "./search-utils";
import { TargetImage } from "domain/targetimage";

const BASE_URL = Config.BASE_PATH + "api/image/";

export function getImage(id: string): Axios.AxiosPromise<TargetImage> {
  return Axios.default.get<TargetImage>(BASE_URL + id);
}

export function imageUrl(t: TargetImage): string {
  return BASE_URL + "file/" + t.id;
}
export function thumbUrl(t: TargetImage): string {
  return BASE_URL + "thumb/" + t.id;
}
export function searchImage(
  q: SearchQuery
): Axios.AxiosPromise<SearchResult<TargetImage>> {
  return Axios.default.get<SearchResult<TargetImage>>(
    BASE_URL + "search" + toSearchQueryString(q)
  );
}

export function saveImage(v: TargetImage): Axios.AxiosPromise<TargetImage> {
  return Axios.default.post<TargetImage>(BASE_URL + "update", v);
}
