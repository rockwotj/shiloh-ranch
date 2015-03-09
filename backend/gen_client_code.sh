if [ $# -eq 0 ]
  then
    echo "No api name"
  else
  	python "c:\Program Files (x86)\Google\google_appengine\endpointscfg.py" get_client_lib java $1
fi
