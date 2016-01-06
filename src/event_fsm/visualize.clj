(ns event-fsm.visualize
  (require [dorothy.core :as d]))

(defn- dot-exists
  "return true if the dot executable from graphviz is available on the path"
  [& _ ]
  (try
    (->> "dot -V"
	 (.exec (Runtime/getRuntime))
	 (.waitFor)
	 (= 0))
    (catch Exception e false)))

(defn- no-graphviz-message []
  (println "The dot executable from graphviz was not found on the path, unable to draw fsm diagrams")
  (println "Download a copy from http://www.graphviz.org/"))

(defn- graphviz-installed? []
  (if (dot-exists)
    true
    (do
      (no-graphviz-message)
      false)))


(defn- dorothy-edge
  "Create a single edge (transition) in a dorothy graph"
  [from-state trans]
  (let [label (str  " " (:evt trans))]
    (vector from-state (:to-state trans) {:label label} )))

(defn- dorothy-state
  "Create a single dorothy state"
  [[state links]]
  (let [is-terminal? (nil? state)]
    [state
     (merge {:label state}
            (when is-terminal?
              {:style "filled,setlinewidth(2)"
               :fillcolor "grey88"}))]))

(defn- transitions-for-state
  "return a sequence of dorothy transitions for a single state"
  [start end [state links]]
  (letfn [(format-trans [from [event to]]
            [(or from start) (or to end) {:label event}])]
    (map #(format-trans state %) links)))

(defn fsm-dorothy
  "Create a dorothy digraph definition for an fsm"
  [fsm]
  (let [start-state (keyword (gensym "start-state"))
        end-state (keyword (gensym "end-state"))
        state-map fsm]
    (d/digraph
      (concat
       [[start-state {:label "start" :style :filled :color :black :shape "point" :width "0.2" :height "0.2"}]]
       [[end-state {:label "start" :style :filled :color :black :shape "point" :width "0.2" :height "0.2"}]]
        (map dorothy-state (dissoc state-map nil))
        (mapcat #(transitions-for-state start-state end-state %) state-map)))))

(defn fsm-dot
  "Create the graphviz dot output for an fsm"
  [fsm]
  (d/dot (fsm-dorothy fsm)))

(defn- show-dorothy-fsm [fsm]
  (d/show! (fsm-dot fsm)))

(defn show [process]
  (show-dorothy-fsm process))
