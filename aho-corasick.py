#!/usr/bin/env python

'''Aho-Corasick algorithm. This algorithm uses a finite automaton to search a
string for multiple patterns.'''

# Adapted from the implementation described at https://algo.pw/algo/64/python

import sys
import signal

# See https://www.tutorialspoint.com/Aho-Corasick-Algorithm

class AhoCorasick(object):
  """Aho-Corasick algorithm. Searches a string for any of
  a number of substrings.

  Usage: Create a list or other iterator of (needle, value) pairs.
      aho_tree = AhoCorasick(needlevaluelist)
      results = aho_tree.findAll(haystack)
      for result in results:
	# Each result is a tuple: (index, length, needle, value)

  values can be literally anything.
  """
  def __init__(self, patternlist=None):
    self.root = None
    if patternlist:
      self.buildStateMachine(patternlist)
  def buildStateMachine(self, patternlist):
    root = self.__buildTree(patternlist)
    queue = []
    for node in root.goto.itervalues():
      queue.append(node)
      node.fail = root
    while queue:
      rnode = queue.pop(0)
      for key, unode in rnode.goto.iteritems():
	queue.append(unode)
	fnode = rnode.fail
	while fnode != None and key not in fnode.goto:
	  fnode = fnode.fail
	unode.fail = fnode.goto[key] if fnode else root
	unode.output += unode.fail.output
    return root
  def findAll(self, string, start=0):
    '''Search this string for items in the dictionary. Return a list of
    (index, len, key, value) tuples.'''
    node = self.root
    for i,ch in enumerate(string[start:]):
      while node is not None and ch not in node.goto:
	node = node.fail
      if node is None:
	node = self.root
	continue
      node = node.goto[ch]
      for word,value in node.output:
	l = len(word)
	yield (i-l+1, l, word, value)
  def __buildTree(self, patternlist):
    """patternlist is a list (or any iterator) of (string,value) pairs."""
    root = AhoCorasick.Node()
    for word,value in patternlist:
      node = root
      for ch in word:
	if ch not in node.goto:
	  node.goto[ch] = AhoCorasick.Node()
	node = node.goto[ch]
      node.output.append((word,value))
    self.root = root
    return root

  class Node(object):
    '''Aho-Corasick algorithm. Each node represents a state in the
    state machine.'''
    def __init__(self):
      self.goto = {}	# Map input to next state
      self.fail = None	# Map state to next state when character doesn't match
      self.output = []	# Map state to all index entries for that state
    def __repr__(self):
      return '<Node: %d goto, %d output>' % \
	(len(self.goto), len(self.output))
    def dump(self, name, indent):
      print "%s%s: AhoCorasickNode: %d goto, output %s, fail=%s" % \
	("  "*indent, name, len(self.goto), self.output, self.fail)
      for k,v in self.goto.iteritems():
	v.dump(k, indent+1)


def main():
  patterns = (('a','Tag a'), ('ab','Tag ab'), ('bc','Tag bc'), ('bca','Tag bca'),
    ('c','Tag c'), ('caa','Tag caa'), ('foo', 'Tag foo'))
  ac = AhoCorasick(patterns)
  #print root
  #root.dump("root", 0)
  s = "abca"
  for result in ac.findAll("abcba"):
    print result
  print
  for result in ac.findAll("foobar"):
    print result

if __name__ == '__main__':
  signal.signal(signal.SIGPIPE, signal.SIG_DFL)
  try:
    sys.exit(main())
  except KeyboardInterrupt, e:
    print
    sys.exit(1)
