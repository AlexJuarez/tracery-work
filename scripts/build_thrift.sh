#!/bin/bash -e

if [ -z "${THRIFT_VER}" ]; then
	THRIFT_VER=0.9.3
	THRIFT_MD5=88d667a8ae870d5adeca8cb7d6795442
	echo "THRIFT_VER not set - defaulting to ${THRIFT_VER}"
fi

THRIFT_HOME=${PWD}/thrift-${THRIFT_VER}
echo "THRIFT_HOME: ${THRIFT_HOME}"

if [ -x ${THRIFT_HOME}/bin/thrift ]; then
	echo "Thrift already built."
	exit 0
else
	echo "Time to build thrift!"
fi

rm -f thrift-${THRIFT_VER}.tar.gz
wget http://www.apache.org/dist/thrift/${THRIFT_VER}/thrift-${THRIFT_VER}.tar.gz
## Verify MD5 since HTTPS to www.apache.org doesn't work reliably.
echo "MD5 (thrift-${THRIFT_VER}.tar.gz) = ${THRIFT_MD5}" > thrift-${THRIFT_VER}.tar.gz.md5
md5sum -c thrift-${THRIFT_VER}.tar.gz.md5
tar xfz thrift-${THRIFT_VER}.tar.gz
mv thrift-${THRIFT_VER} thrift-${THRIFT_VER}-build
pushd ./thrift-${THRIFT_VER}-build
./configure --prefix=${THRIFT_HOME} --disable-libs --disable-tests --disable-tutorial
make -j`nproc --all` install
popd
rm -rf thrift-${THRIFT_VER}-build thrift-${THRIFT_VER}.tar.gz thrift-${THRIFT_VER}.tar.gz.md5
