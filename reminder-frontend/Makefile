.PHONY: docker
docker:
	rm -rf workspace
	npm run build
	mkdir workspace
	cp ./Dockerfile workspace/Dockerfile
	cp -r ./static workspace/static
	cd workspace
	docker build -t keisunagawa/reminder-frontend .
	docker push keisunagawa/reminder-frontend:latest
	rm -rf workspace
