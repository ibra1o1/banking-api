.PHONY: build start run

VERSION?=0.1
DOCKERNAME?=banking
DOCKERTAG?=${DOCKERNAME}:${VERSION}

build: ## Build the container
	docker build -t $(DOCKERTAG) .

start: ## Start the container
	docker run -p 8080:8080 ${DOCKERTAG}

run: build start


