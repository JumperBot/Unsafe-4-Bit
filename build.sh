sudo rm _site -r
sudo rm docs -r
bundler exec jekyll build
cd _site
find . -type f -exec sed -i -e 's/"\/assets\/css\/style.css/".\/assets\/css\/style.css/g' {} \;
find . -type f -exec sed -i -e 's/<!-- link rel="shortcut icon" type="image\/x-icon" href="\/favicon.ico" -->/<link rel="icon" href="favicon.svg" sizes="any" type="image\/svg+xml">/g' {} \;
mkdir ../docs
mv * ../docs
cd ..
rmdir _site
