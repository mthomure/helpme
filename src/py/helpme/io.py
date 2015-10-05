import transit.reader
import transit.writer

# https://github.com/cognitect/transit-python

TRANSIT_PROTOCOL = "json"

def read_transit(fname, protocol=TRANSIT_PROTOCOL):
    with open(fname) as fh:
        rdr = transit.reader.Reader(protocol)
        return rdr.read(fh)

def write_transit(fname, obj, protocol=TRANSIT_PROTOCOL):
    with open(fname, 'w') as fh:
        wrt = transit.writer.Writer(fh, protocol)
        wrt.write(obj)
