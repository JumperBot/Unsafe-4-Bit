sudo rm _site -r
sudo rm docs -r
bundler exec jekyll build
cd _site
find . -type f -exec sed -i -e 's/"\/assets\/css\/style.css/".\/assets\/css\/style.css/g' {} \;
rm build.sh
mkdir ../docs
mv * ../docs
cd ..
rmdir _site
