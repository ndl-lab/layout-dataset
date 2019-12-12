import * as Axios from "axios";
import * as Config from "config";
import { ImageBinder } from "domain/imagebinder";
import { SearchQuery, SearchResult, toSearchQueryString } from "./search-utils";

const BASE_URL = Config.BASE_PATH + "api/binder/";

export function getBinder(id: string): Axios.AxiosPromise<ImageBinder> {
  return Axios.default.get<ImageBinder>(BASE_URL + id);
}

export function searchBinder(
  q: SearchQuery
): Axios.AxiosPromise<SearchResult<ImageBinder>> {
  return Axios.default.get<SearchResult<ImageBinder>>(
    BASE_URL + "search" + toSearchQueryString(q)
  );
}

export function saveBinder(v: ImageBinder): Axios.AxiosPromise<ImageBinder> {
  return Axios.default.post<ImageBinder>(BASE_URL + "update", v);
}
