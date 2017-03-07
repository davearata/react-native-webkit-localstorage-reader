import React from 'react-native';

const WebkitLocalStorageReader = React.NativeModules.WebkitLocalStorageReader;

export default {
  get: () => {
    return new Promise((resolve, reject) => {
      WebkitLocalStorageReader.get((jsonString) => {
        if (jsonString) {
          try {
            const jsonObj = JSON.parse(jsonString)
            return resolve(jsonObj)
          } catch (err) {
            return reject(err)
          }
        }
        resolve()
      })
    })
  }
};
