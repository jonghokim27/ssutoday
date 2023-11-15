import React from "react";
import { StyleSheet } from "react-native";
import { Camera, useCameraDevices } from "react-native-vision-camera";

const CameraView = React.forwardRef((props, ref) => {
    const devices = useCameraDevices('wide-angle-camera')
    const device = devices.back

    if (device == null) return <></>
    return (
        <Camera
        style={StyleSheet.absoluteFill}
        device={device}
        ref={ref}
        // ref={(ref) => this.camera = ref}
        photo={true}
        video={false}
        audio={false} // 선택사항
        isActive={props.isActive}
        />
    )
});

export default CameraView;