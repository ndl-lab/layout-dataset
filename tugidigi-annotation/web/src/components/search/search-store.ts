import * as Axios from "axios";
import {
  APICallType,
  fromQueryString,
  GeoDistance,
  Range,
  SearchQuery,
  SearchResult,
  toSearchQueryString,
  toSeqrchQueryUrlMap
} from "service/search-utils";
import { setLoading } from "types";
import { clone, isEmpty } from "utils/objects";
import * as URL from "utils/url";

interface SearchMemory {
  q: string;
  r: SearchResult<any>;
}

const SSKEY_SEARCH_CACHE = "search-cache";

export default class SearchStore<T> {
  public result: SearchResult<T> = null;
  protected searching: boolean = false;

  silent: boolean = false;
  cacheSubKey: string = "default";

  get cacheKey() {
    return (
      SSKEY_SEARCH_CACHE +
      "_" +
      window.location.pathname +
      "_" +
      this.cacheSubKey
    );
  }

  keywords: string[] = [];
  keywordType: "AND" | "OR" = "AND";

  query: { [key: string]: string[] } = {};
  image: string[] = [];
  range: { [key: string]: Range[] } = {};
  geoDistance: {
    [key: string]: GeoDistance[];
  } = {};
  filter: { [key: string]: string[] } = {};
  facet: { [key: string]: string[] } = {};
  from: number = 0;
  size: number = 50;
  sort: string[] = [""];
  exists: string[] = [];

  permission: APICallType = null;

  clearQuery() {
    this.keywords = [];
    this.keywordType = "AND";
    this.query = {};
    this.image = [];
    this.range = {};
    this.geoDistance = {};
    this.filter = {};
    this.facet = {};
    this.from = 0;
    this.size = 50;
    this.sort = [""];
    this.exists = [];
    this.syncQuery();
  }
  constructor(
    public searchService: (
      q: SearchQuery
    ) => Axios.AxiosPromise<SearchResult<T>>,
    private history: boolean = true
  ) {
    if (this.history) {
      URL.addPopStateListener(() => {
        this.restoreQuery();
      });
    }
  }

  get hit(): number {
    if (this.result) return this.result.hit;
    return 0;
  }

  get list(): any[] {
    if (this.result) return this.result.list;
    return [];
  }

  get searched() {
    return this.result !== null;
  }

  clear = () => {
    this.result = null;
  };

  /**
   * クラス外から呼ばれるため、thisのcontextが変わってしまう。そのため、ラムダ式で回避
   * */
  search = (query: { [key: string]: string[] }) => {
    this.query = query;
    this.execute();
  };

  searchItem = (keyword: string[], query: { [key: string]: string[] }) => {
    this.keywords = keyword;
    this.query = query;
    this.from = 0;
    this.facet = {};
    this.execute();
  };

  setSort = (sort: string[]) => {
    this.sort = sort;
    this.execute();
  };

  setSize = (pagesize: number) => {
    if (pagesize > 0) {
      this.size = pagesize;
      this.execute();
    }
  };

  setFrom = (from: number) => {
    if (from >= 0) {
      this.from = from;
      this.execute();
    }
  };

  removeFacet = (def: string, words: string[]) => {
    if (this.facet[def]) {
      words.forEach(word =>
        this.facet[def].splice(this.facet[def].indexOf(word), 1)
      );
      this.from = 0;
      this.execute();
    }
  };

  addFacet = (def: string, words: string[]) => {
    if (!this.facet[def]) this.facet[def] = [];
    words.forEach(word => {
      if (!this.facet[def].includes(word)) this.facet[def].push(word);
    });
    this.from = 0;
    this.execute();
  };

  setFacet = (def: string, words: string[]) => {
    this.facet[def] = words;
    this.from = 0;
    this.execute();
  };

  execute(detailed = true, errorCallback: (r) => void = null) {
    this.beforeSearchListener.forEach(f => f(this, detailed));
    let q: SearchQuery = new SearchQuery();
    if (this.from != null) q.from = this.from;
    if (this.size != null) q.size = this.size;
    if (!isEmpty(this.sort)) q.sort = this.sort;
    if (this.keywordType === "OR") q.keywordOR = true;

    if (!isEmpty(this.keywords)) q.keyword = clone(this.keywords);
    if (!isEmpty(this.query)) q.query = clone(this.query);
    if (!isEmpty(this.range)) q.range = clone(this.range);
    if (!isEmpty(this.facet)) q.facet = clone(this.facet);
    if (!isEmpty(this.image)) q.image = clone(this.image);
    if (!isEmpty(this.filter)) q.filter = clone(this.filter);
    if (!isEmpty(this.exists)) q.exists = clone(this.exists);
    if (!isEmpty(this.geoDistance)) q.geoDistance = clone(this.geoDistance);

    this.executeSearch(q, errorCallback);
  }

  getQuery(detailed = true): SearchQuery {
    this.beforeSearchListener.forEach(f => f(this, detailed));
    let q: SearchQuery = new SearchQuery();
    if (this.from != null) q.from = this.from;
    if (this.size != null) q.size = this.size;
    q.sort = this.sort;
    if (this.keywordType === "OR") q.keywordOR = true;

    q.keyword = clone(this.keywords);
    q.query = clone(this.query);
    q.range = clone(this.range);
    q.facet = clone(this.facet);
    q.filter = clone(this.filter);
    q.exists = clone(this.exists);
    q.geoDistance = clone(this.geoDistance);
    return q;
  }

  beforeSearchListener: ((q: SearchStore<T>, detailed: boolean) => void)[] = [];

  addBeforeSearchListenr(
    listener: (q: SearchStore<T>, detailed: boolean) => void
  ) {
    this.beforeSearchListener.push(listener);
  }

  queryListeners: ((ss: SearchStore<T>) => void)[] = [];

  addQueryListenr(listener: (ss: SearchStore<T>) => void) {
    this.queryListeners.push(listener);
  }

  syncQuery() {
    this.queryListeners.forEach(f => f(this));
  }

  afterSearchListener: ((result: SearchResult<T>) => void)[] = [];

  addAfterSearchListener(listener: (result?: SearchResult<T>) => void) {
    this.afterSearchListener.push(listener);
  }

  restoreQuery(qu?: SearchQuery) {
    if (window.location.search || qu) {
      let q = this.restoreQueryWithoutExecute(qu);
      if (q != null) this.executeSearch(q, null, false);
    } else {
      this.result = null;
    }
  }

  restoreQueryWithoutExecute(qu?: SearchQuery): SearchQuery {
    let q = qu ? qu : fromQueryString(window.location.search);
    this.from = q.from || 0;
    this.size = q.size || 50;
    this.sort = q.sort || [];
    this.keywords = q.keyword || [];
    this.image = q.image || [];
    this.query = q.query || {};
    this.facet = q.facet || {};
    this.filter = q.filter || {};
    this.exists = q.exists || [];
    this.keywordType = q.keywordOR ? "OR" : "AND";
    this.syncQuery();
    return q;
  }

  restoreQueryAndResult(qu: SearchQuery, result: SearchResult<T>) {
    this.restoreQueryWithoutExecute(qu);
    this.currentQuery = qu;
    this.setSearchResult(result, false);
  }

  private setSearchResult(
    result: SearchResult<T>,
    byIntent: boolean = true
  ): void {
    this.result = result;
    this.afterSearchListener.forEach(f => f(result));
    if (byIntent) {
      if (this.history && this.currentQuery) {
        let q = URL.getQueryMap();
        let urlMap = toSeqrchQueryUrlMap(this.currentQuery);
        window.history.pushState(null, null, URL.toQueryString(urlMap));
      }
    }
  }

  currentQuery: SearchQuery;
  private currentQueryStr: string;
  nocache: boolean = false;

  private executeSearch(
    q: SearchQuery,
    errorCallback: (r) => void = null,
    withIntent: boolean = true
  ) {
    let qstr = toSearchQueryString(q);
    // if (equalsIgnoreNull(this.currentQueryStr, qstr)) return;
    this.searching = true;
    setLoading(true);
    this.searchService(q)
      .then(result => {
        this.currentQueryStr = qstr;
        this.currentQuery = q;
        this.setSearchResult(result.data, withIntent);
        this.searching = false;
        setLoading(false);
      })
      .catch(result => {
        this.setSearchResult(null);
        this.searching = false;
        // errorCallback(result);
        setLoading(false);
      });
  }

  checkNewSEarch(qstr: string) {
    let qstrWithoutPage = qstr.replace(/&?from=\d+|&?size=\d+/g, "");
    console.info("check new search", qstrWithoutPage);
  }
}
