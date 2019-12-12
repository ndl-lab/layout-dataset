import * as Axios from "axios";
import * as Config from "config";
import { ImageType } from "domain/imagetype";

const BASE_URL = Config.BASE_PATH + "api/type/";

export function getType(id: string): Axios.AxiosPromise<ImageType> {
  return Axios.default.get<ImageType>(BASE_URL + id);
}

export function listType(): Axios.AxiosPromise<ImageType[]> {
  return Axios.default.get<ImageType[]>(BASE_URL + "all");
}
