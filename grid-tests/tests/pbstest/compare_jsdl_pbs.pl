#! /usr/bin/perl

use IO::File;
use XML::Parser;

# help blurb
if (!$ARGV[0] || !$ARGV[1])
  {
  print "\nCheck consistency between a JDSL file and one or more PBS job scripts.\n\n";
  print "Useage:\n\tparse_scripts jdsl_file pbs_files\n\n";
  exit;
  }

# data storage hashes
my %table_pbs;
my %table_xml;

# JDSL parse
$file = shift;
my @context_stack;
my @text_stack;
my $text = new IO::File ($file);
my $parser = new XML::Parser (Handlers => {
                              Start   => \&element_open,
                              End     => \&element_close,
                              Char    => \&text_data,
                              Default => \&catch_default,
                              });
print "Parsing XML script: $file\n";
$parser->parse($text);

# PBS parses(s)
my $status=0;
foreach $file (@ARGV)
  {
print "Parsing PBS script: $file\n";

# clear the PBS hash before filling it out
  %table_pbs = ();
  parse_pbs($file);

# compare data in the hashes
  $status += parse_compare();
  }

print "status= $status\n";

# all done
exit $status;


# --- handle element open
sub element_open
  {
  my ($expat, $element, %atts) = @_;

  if ($element eq 'LowerBoundedRange')
    {
    push (@context_stack, $element);
    }
  }

# --- handle element close
sub element_close
  {
  my ($expat, $element) = @_;

  my $context = pop (@context_stack);

  if ($context eq "")
    {
    my $text = pop (@text_stack); 
    $table_xml{$element} = $text;
    }
  else
    {
# TODO - figure out how to properly handle things like lowerboundedrange
    }
  }

# --- handle text data 
sub text_data
  {
  my ($expat, $text) = @_;

  push(@text_stack, $text);
  }

# --- handle misc
sub catch_default { }

# --- parse PBS scripts
sub parse_pbs
{
$file = shift;

#print "parsing: $file ...\n";

open (INP,$file) || die "Error in parse_pbs(): cannot open $file\n";

while (<INP>)
  {
# look for lines starting with PBS directives
  if (/^#PBS/)
    {
# extract line tokens (remove multiple whitespaces) 
    split /\s+/;
    if (@_[1] eq "-l")
      {
      $_ = @_[2];
      if (/\=/)
        {
# assumes no spaces (eg vmem=1000mb) 
        @tmp = split('=', @_[2]);
        $type = @tmp[0];
        $value = @tmp[1];
        $table_pbs{$type} = $value;
        }
      }
    }

  if (/^module load/)
    {
    split /\s+/;
    $table_pbs{@_[0]} = @_[2];
    }

  }
}

# --- convert PBS walltime to seconds
sub pbs_walltime_seconds
{
$_ = shift;

#print "in: $_\n";

my @tokens = reverse split(':');

$time = @tokens[0] + @tokens[1]*60 + @tokens[2]*60*60;

#print "out: $time\n";

return $time;
}

# --- convert PBS memory to bytes
sub pbs_memory_bytes
{
$_ = shift;

#print "in: $_\n";

if (/kb/)
  {
  s/kb//;
  $_ *= 1024;
  }

if (/mb/)
  {
  s/mb//;
  $_ *= 1024*1024;
  }

if (/gb/)
  {
  s/gb//;
  $_ *= 1024*1024*1024;
  }

#print "out: $_\n";

return $_;
}

# --- compare hash table values of XML with PBS 
sub parse_compare
{
#parse_debug();

# set of tests/rules for comparing 
# ref - http://projects.arcs.org.au/trac/grisu/wiki/GlobusToolkitSubmitter
$cpu_xml = $table_xml{"TotalCPUCount"};
$cpu_pbs = $table_pbs{"ncpus"};

# JSDL - element in bytes
# PBS - units attached (eg 100mb)
$mem_xml = $table_xml{"TotalPhysicalMemory"};
$tmp = $table_pbs{"vmem"};
$mem_pbs = pbs_memory_bytes($tmp);

# JSDL - in seconds
# PBS - in h:m:s
$time_xml = $table_xml{"TotalCPUTime"};
$tmp = $table_pbs{"walltime"};
$time_pbs = pbs_walltime_seconds($tmp);

$module_xml = $table_xml{"Module"};
$module_pbs = $table_pbs{"module"};

# TODO - mpi / mpirun test
my $status=0;

# expect pbs request = xml request
$status += parse_test("   CPU", $cpu_xml, $cpu_pbs);

# expect pbs request = xml request
$status += parse_test("  Time", $time_xml, $time_pbs);

# expect pbs request to be xml request x cpus
$status += parse_test("Memory", $cpu_xml*$mem_xml, $mem_pbs);

# expect pbs request = xml request
$status += parse_test("Module", $module_xml, $module_pbs);

return $status;
}

# --- simple pass/fail test message
sub parse_test
{
$text = shift;
$a = shift;
$b = shift;

if ($a eq $b)
  {
  print "PASS : $text (XML=$a) (PBS=$b)\n";
  return 0;
  }
else
  {
  print "FAIL : $text (XML=$a) (PBS=$b)\n";
  return 1;
  }
}

# --- dump hash tables
sub parse_debug
{
while (($key, $value) = each(%table_xml))
  {
  print "XML: $key == $value\n";
  }
while (($key, $value) = each(%table_pbs))
  {
  print "PBS: $key == $value\n";
  }
}
