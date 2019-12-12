// import * as Axios from "axios";
// import { deepFind } from "utils/objects";

// //TODO local-storageを使う？
// //TODO cache-sizeの制限？
// let cache = {};

// export function putCache(type: string, id: string, value: any) {
//   if (cache[type] == null) cache[type] = {};
//   cache[type][id] = value;
// }

// /**
//  *
//  * 指定されたtype、idのGETが過去に行われている場合、キャッシュ（メモリ）から応答する。
//  * 行われていなかった場合、指定されたURLでGETを行い、値をキャッシュに格納する。
//  *
//  * @param type キャッシュの種類
//  * @param id ID
//  * @param getUrl HTTP GETを発行するURL
//  *
//  */
// export function cacheGet<T>(
//   type: string,
//   id: string,
//   getUrl: string
// ): Promise<T> {
//   if (cache[type] == null) cache[type] = {};
//   return new Promise<T>((resolve, reject) => {
//     //キャッシュにヒットした場合
//     if (cache[type][id]) {
//       //            console.info("cache-hit", type, id);
//       if (cache[type][id] instanceof Promise) {
//         let promise = <Axios.AxiosPromise<T>>cache[type][id];
//         promise
//           .then(result => {
//             //                    console.info("chained-success");
//             resolve(result.data);
//             return result;
//           })
//           .catch(result => {
//             //                    console.info("chained-fail");
//             reject(result);
//             return Promise.reject(result);
//           });
//       } else {
//         //                console.info("cache-hit-object");
//         resolve(cache[type][id]);
//       }
//     } else {
//       //            console.info("cache-nonhit", type, id);
//       let promise = Axios.default
//         .get(getUrl)
//         .then(result => {
//           //                console.info("first-success");
//           cache[type][id] = result.data;
//           resolve(result.data);
//           return result;
//         })
//         .catch(result => {
//           //                console.info("first-fail");
//           cache[type][id] = null;
//           reject(result);
//           return Promise.reject(result);
//         });
//       if (cache[type][id] == null) {
//         cache[type][id] = promise;
//       }
//     }
//   });
// }

// /**
//  *
//  * 指定されたtype、idのキャッシュを削除する（データの更新があった時など）。
//  *
//  * @param type キャッシュの種類
//  * @param id ID
//  *
//  */
// export function clearCache(type: string, id: string) {
//   if (cache[type]) cache[type][id] = null;
// }
