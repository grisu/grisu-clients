import Blender
import sys
import os

print "StartFrame: %d" % (Blender.Get('staframe'))
print "EndFrame: %d" % (Blender.Get('endframe'))

#
# Print the paths to resources that are accessible through the Blender Python
# API.
#
print "Known resources:"
for path in Blender.GetPaths(True):
	print "Resource: " + path

#
# Guess the paths of physics cache files.
#
try:
	frame = int(sys.argv[-1])
except ValueError:
	sys.exit()

print "Cache files, guessed for frame %d:" % frame


print frame

