import React from 'react';
import {View} from 'react-native';
import {useSafeAreaInsets} from 'react-native-safe-area-context';

function BottomSafe(props) {
  const insets = useSafeAreaInsets();

  return (
    <View
      style={{
        height: insets.bottom > 0 ? Math.ceil(insets.bottom / 2) : 0,
        backgroundColor: props.backgroundColor ? props.backgroundColor : 'white',
      }}
    />
  );
}

export default BottomSafe;
