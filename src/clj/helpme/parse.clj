(ns helpme.parse
  (:import org.joda.time.Interval)
  (:require [clj-time.core :as t]))

(defn interval
  "Parse a Joda-Time interval from string s, optionally relative to timezone
  tz. Assumes local timezone if none is specified.

  Useful examples:

  The one-day interval from Nov 1st to Nov 2nd, 2015.

  (interval \"2015-11-01/2015-11-02\")

  Either endpoint can be a period.

  (interval \"2015-11-01/P1D\")
  (interval \"P1D/2015-11-02\")

  The six-hour interval from midnight to 6am on Nov 1st, 2015.

  (interval \"2015-11-01/2015-11-01T06\")
  (interval \"2015-11-01/P6H\")

  Specify eastern timezone (as an argument).

  (let [tz (t/time-zone-for-id \"America/New_York\")]
    (interval \"2015-11-01/2015-11-02\" tz))

  Specify eastern standard timezone (in the string).

  (interval \"2015-11-01T-05/2015-11-02T-05\")

  See also: https://en.wikipedia.org/wiki/ISO_8601"
  ([s]
   (org.joda.time.Interval/parse s))
  ([s tz]
   (let [ival (interval s)]
     (t/interval (t/from-time-zone (t/start ival) tz)
                 (t/from-time-zone (t/end ival) tz)))))

(defn date-time
  ([s]
   (org.joda.time.DateTime/parse s))
  ([s tz]
   (t/from-time-zone (date-time s) tz)))
