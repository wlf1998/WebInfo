index:index.cpp
	@g++ index.cpp -o index

test:
	@./index $(n)
	
	@echo -e "**** index.txt ****"
	@cat index.txt

	@echo -e "\n\n**** dictionary.txt ****"
	@cat dictionary.txt

	@echo -e  "\n\n**** postingsList.txt ****"
	@cat postingsList.txt

clean:
	@rm -rf index index.txt dictionary.txt postingsList.txt