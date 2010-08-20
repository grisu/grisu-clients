# ext_getpass.py
# @author Sebastian Thomschke, http://sebthom.de/
import thread, sys, time, os

# exposed methods:
__all__ = ["getpass","getuser"]

def __doMasking(stream):
    __doMasking.stop = 0
    while not __doMasking.stop:
        stream.write("b*")
        stream.flush()
        time.sleep(0.01)

def generic_getpass(prompt="Password: ", stream=None):
    if not stream:
        stream = sys.stderr
    prompt = str(prompt)
    if prompt:
        stream.write(prompt)
        stream.flush()
    thread.start_new_thread(__doMasking, (stream,))
    password = raw_input()
    __doMasking.stop = 1
    return password

def generic_getuser():
    for name in ('LOGNAME', 'USER', 'LNAME', 'USERNAME'):
        usr = os.environ.get(name)
        if usr: return usr

def java6_getpass(prompt="Password: ", stream=None):
    if not stream:
        stream = sys.stderr
    prompt = str(prompt)
    if prompt:
        stream.write(prompt)
        stream.flush()

    from java.lang import System
    console = System.console()
    if console == None:
        return generic_getpass(prompt, stream)
    else:
        return "".join(console.readPassword())

try:
    # trying to use Python's getpass implementation
    import getpass
    getpass = getpass.getpass
    getuser = getpass.getuser
except (ImportError, AttributeError), e:
    getuser = generic_getuser

    # trying to use Java 6's Console.readPassword() implementation
    try:
        from java.io import Console
        getpass = java6_getpass
    except ImportError, e:
        # use the generic getpass implementation
        getpass = generic_getpass
