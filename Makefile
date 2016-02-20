VERSION=0.0.1
PROJECT=lcmap-see
STANDALONE=target/$(PROJECT)-$(VERSION)-SNAPSHOT-standalone.jar
ROOT_DIR = $(shell pwd)

build: clean
	@lein compile
	@lein uberjar

standalone: build
	java -jar $(STANDALONE)

standalone-heavy: build
	java -Xms3072m -Xmx3072m -jar $(STANDALONE)

run:
	@lein run

test-auth-server:
	@cd test/support/auth-server && lein with-profile +dev run

shell:
	@lein repl

repl:
	@lein repl

clean-all: clean clean-docs clean-docker

clean:
	@rm -rf target
	@rm -f pom.xml

deps-tree:
	@lein pom
	@mvn dependency:tree

include resources/make/docs.mk

