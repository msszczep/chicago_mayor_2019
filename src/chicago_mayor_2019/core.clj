(ns chicago-mayor-2019.core)


(defn split-on-pipe [line]
  (mapv clojure.string/trim (clojure.string/split line #"\|")))


(defn slurp-file [f]
  (->> f
       slurp
       clojure.string/split-lines
       (mapv split-on-pipe)))


(defn csv-data->maps [csv-data]
  (map zipmap
       (->> (first csv-data) ;; First row is the header
            (map keyword) ;; Drop if you want string keys instead
            repeat)
	  (rest csv-data)))

(def data-to-use 
  (slurp-file "resources/data.txt"))

(def scores-to-use 
  (slurp-file "resources/scorecard.txt"))

(def data-as-map
  (csv-data->maps data-to-use))

(def scores-as-map
  (csv-data->maps scores-to-use))

(def initials-to-names
  {:TP "Toni Preckwinkle"
   :LF "LaShawn Ford"
   :AE "Amara Enyia"
   :GC "Gery Chico"
   :PV "Paul Vallas"
   :BF "Bob Fioretti"
   :JJ "Jerry Joyce"
   :GM "Garry McCarthy"
   :SM "Susana Mendoza"
   :WW "Willie Wilson"
   :LL "Lori Lightfoot"
   :BD "Bill Daley"
   :JK "John Kozlar"})

(defn convert-answer-to-number [candidate [question answer]]
  (let [scores (->> scores-as-map
                    (filter (comp (partial = question) :Can))
                    first)]
   (->> data-as-map
        (filter (comp (partial = question) :Can))
        first
        candidate
        keyword
        scores
        Integer.)))


(def final-tally
  (for [candidate (keys initials-to-names)]
    (let [answer (map (juxt :Can candidate) data-as-map)
          nums-to-use (map (partial convert-answer-to-number 
                                      candidate) 
                            answer)]
      [(initials-to-names candidate) 
       (apply + nums-to-use) 
       nums-to-use])))


(pprint (reverse (sort-by second final-tally)))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
