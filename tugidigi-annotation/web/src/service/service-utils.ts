import * as Axios from "axios";

export function toPromise<T>(ap: Axios.AxiosPromise<T>): Promise<T> {
  return new Promise<T>((resolve, reject) => {
    ap.then(result => {
      resolve(result.data);
      return result.data;
    }).catch(result => {
      reject(result);
      Promise.reject(result);
    });
  });
}

export function getAll<T>(
  ids: string[],
  getter: (id: string) => Promise<T>
): Promise<T[]> {
  return Promise.all(ids.map(id => getter(id)));
}
