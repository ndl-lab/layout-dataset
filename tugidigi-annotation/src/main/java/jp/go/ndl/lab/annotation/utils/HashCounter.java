package jp.go.ndl.lab.annotation.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HashCounter<K> {



    public HashCounter() {
    }

    public Set<K> getKeySet() {
        return countMap.keySet();
    }

    private class Count {

        @Override
        public String toString() {
            return "" + count;
        }

        /**
         * 総称型インスタンス
         */
        private K key;

        /**
         * カウント値
         */
        private int count = 0;

        /**
         * コンストラクタ
         *
         * @param key 総称型
         */
        Count(K key) {
            this.key = key;
        }
    }
    /**
     * カウンタ格納マップ
     */
    private final HashMap<K, Count> countMap = new LinkedHashMap<>();

    /**
     * カウンタ格納マップの指定したキーに対応するカウントを+1する。
     * @param key キー
     * @return カウンタ格納マップの指定したキーに対応するカウントを+1
     */
    public int countPlus(K key) {
        if (key == null) return 0;
        Count c = countMap.get(key);
        if (c == null) {
            c = new Count(key);
            countMap.put(key, c);
        }
        return ++c.count;
    }

    /**
     * カウンタ格納マップの指定したキーに対応するカウントを-1する。
     * @param key キー
     */
    public void countMinus(K key) {
        if (key == null) return;
        Count c = countMap.get(key);
        if (c == null) {
            c = new Count(key);
            countMap.put(key, c);
        }
        c.count--;
    }

    /**
     * カウンタ格納マップの指定したキーに対応するカウントに指定したカウントを加算する。
     * @param key キー
     * @param count カウント
     */
    public void countValue(K key, int count) {
        if (key == null) return;
        Count c = countMap.get(key);
        if (c == null) {
            c = new Count(key);
            countMap.put(key, c);
        }
        c.count += count;
    }

    /**
     * カウンタ格納マップの指定したキーに対応するカウントを取得する。
     * @param key countMapキー
     * @return キーに対応する要素
     */
    public int getCount(K key) {
        if (key == null) return 0;
        Count c = countMap.get(key);
        if (c == null) {
            return 0;
        } else {
            return c.count;
        }
    }

    /**
     * カウンタ格納マップからカウントの昇順で取得件数分のカウンタ格納マップを取得する。
     *
     * @param x 取得件数
     * @return 取得件数分のカウンタ格納マップ
     */
    public Map<K, Integer> topX(int x) {
        List<Count> countList = new ArrayList<>(countMap.values());
        Collections.sort(countList, (Count o1, Count o2) -> o2.count - o1.count);

        Map<K, Integer> map = new LinkedHashMap<>();
        int limit = Math.min(x, countList.size());
        for (int i = 0; i < limit; i++) {
            Count count = countList.get(i);
            map.put(count.key, count.count);
        }
        return map;
    }

    /**
     * 標準出力にカウンタ格納マップに格納されているキーとカウントをカウントの昇順で出力する。
     */
    public void printCount() {
        List<Count> countList = new ArrayList<>(countMap.values());
        Collections.sort(countList, (Count o1, Count o2) -> o1.count - o2.count);
        for (Count c : countList) {
            System.out.println(c.key + "\t" + c.count);
        }
    }

    /**
     * 標準出力にカウンタ格納マップに格納されているキーとカウント、カウントの合計をカウントの昇順で出力する。
     */
    public void printCount2() {
        List<Count> countList = new ArrayList<>(countMap.values());
        Collections.sort(countList, (Count o1, Count o2) -> o1.count - o2.count);
        int i1 = 0;
        int i2 = 0;
        for (Count c : countList) {
            i1 += (Integer) c.count;
            int sum = ((Integer) c.key) * c.count;
            i2 += sum;
            System.out.println(c.key + "\t" + c.count + "\t" + sum);
        }
        System.out.println("\t" + i1 + "\t" + i2);
    }

    /**
     * 標準出力にカウンタ格納マップに格納されているキーとカウント、カウントの合計をカウントの降順で出力する。
     */
    public void printCount3() {
        List<Count> countList = new ArrayList<>(countMap.values());
        Collections.sort(countList, (Count o1, Count o2) -> (Integer) o2.key - (Integer) o1.key);
        int i1 = 0;
        int i2 = 0;
        for (Count c : countList) {
            i1 += (Integer) c.count;
            int sum = ((Integer) c.key) * c.count;
            i2 += sum;
            System.out.println(c.key + "\t" + c.count + "\t" + sum);
        }
        System.out.println("\t" + i1 + "\t" + i2);
    }

    /**
     * カウンタ格納マップに格納されている値の合計値を返却する。
     * @return カウント合計値
     */
    public int getTotal() {
        int total = 0;
        for (Count c : countMap.values()) {
            total += c.count;
        }
        return total;
    }
}
