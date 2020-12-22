all:
	npm install
	ts-node hello-world.ts > config.json
	cat config.json

deploy:
	cd .. && sbt "run local developerPlayground ./mvp/config.json /DEV/playground/hello-world-app/"

clean:
	rm -rf config.json node_modules package-lock.json
