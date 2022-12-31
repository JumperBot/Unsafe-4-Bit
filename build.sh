sudo rm _site -r
bundler exec jekyll build
cd _site
find . -type f -exec sed -i -e 's/"\/assets\/css\/style.css/".\/assets\/css\/style.css/g' {} \;
rm build.sh
