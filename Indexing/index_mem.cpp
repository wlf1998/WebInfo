#include<iostream>
#include<vector>
#include<string>
#include<fstream>

using namespace std;

int main()
{
    ifstream in;
    in.open("./doc/1.txt");
    char buf[100];
    cout<<in.tellg()<<endl;
    in.getline(buf,7);
    cout<<buf<<endl;
    cout<<in.tellg()<<endl;
}
