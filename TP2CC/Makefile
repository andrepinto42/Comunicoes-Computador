INPUT = /syncing

default:
	javac -d . src/*.java
clean:
	rm src/*.class
client:
	java bin.EchoClient $(filter-out $@,$(MAKECMDGOALS))
server:
	java bin.EchoServer $(filter-out $@,$(MAKECMDGOALS))

# Esta linha é necessario pois ao adicionar argumentos extra na make
# Não queremos que eles sejam executados
%:
	@: