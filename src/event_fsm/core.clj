(ns event-fsm.core
  (require [event-fsm.visualize :as v]))

(def process1
  {nil {:event1 :1}
   :1 {:event2 :2}
   :2 {:event3 :3
       :event4 :4
       :event5 :5}
   :3 {:event1 :1
       :event5 :6}
   :4 {:event5 :6
       :event1 :1}
   :5 {:event6 :5
       :event7 :6
       :event1 :1}
   :6 {:event8 nil}})

(defn next-state
  [process cur-state event]
  (get-in process [cur-state event]))

;; (v/show process1)


