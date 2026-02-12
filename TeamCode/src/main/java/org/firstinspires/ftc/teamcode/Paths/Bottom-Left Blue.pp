{
  "startPoint": {
    "x": 56,
    "y": 8,
    "heading": "linear",
    "startDeg": 90,
    "endDeg": 0,
    "locked": false
  },
  "lines": [
    {
      "id": "mk5zimhe-rmc7dy",
      "name": "Go to Launch zone",
      "endPoint": {
        "x": 49.5,
        "y": 93.5,
        "heading": "linear",
        "reverse": false,
        "startDeg": 90,
        "endDeg": 135
      },
      "controlPoints": [],
      "color": "#B7C8D6",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "mk6014zb-9qa4nj",
      "name": "Down to artifacts Row 2",
      "endPoint": {
        "x": 49.5,
        "y": 60,
        "heading": "linear",
        "reverse": false,
        "startDeg": 135,
        "endDeg": 180
      },
      "controlPoints": [],
      "color": "#AD6BC9",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "mk6025dt-du92xt",
      "name": "Pickup artifacts 2 ",
      "endPoint": {
        "x": 27,
        "y": 60,
        "heading": "linear",
        "reverse": false,
        "startDeg": 180,
        "endDeg": 180
      },
      "controlPoints": [],
      "color": "#A65BC8",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "ml9ytebc-zfuo86",
      "endPoint": {
        "x": 49.5,
        "y": 60,
        "heading": "linear",
        "reverse": false,
        "startDeg": 180,
        "endDeg": 180
      },
      "controlPoints": [],
      "color": "#AC8D6C",
      "name": "Backup",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "mk6054nm-53eoae",
      "name": "Up to launch zone",
      "endPoint": {
        "x": 49.5,
        "y": 93.5,
        "heading": "linear",
        "reverse": false,
        "startDeg": 180,
        "endDeg": 135
      },
      "controlPoints": [],
      "color": "#A577A7",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "ml9yw9d2-5fzj6e",
      "name": "",
      "endPoint": {
        "x": 49.5,
        "y": 36,
        "heading": "linear",
        "reverse": false,
        "startDeg": 135,
        "endDeg": 180
      },
      "controlPoints": [],
      "color": "#ADA868",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "ml9yxgbs-0oo0xp",
      "name": "Path 7",
      "endPoint": {
        "x": 27,
        "y": 36,
        "heading": "linear",
        "reverse": false,
        "startDeg": 180,
        "endDeg": 180
      },
      "controlPoints": [],
      "color": "#69789C",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "ml9yyvfk-xut3ac",
      "name": "Path 8",
      "endPoint": {
        "x": 49.5,
        "y": 93.5,
        "heading": "linear",
        "reverse": false,
        "startDeg": 180,
        "endDeg": 135
      },
      "controlPoints": [],
      "color": "#C6B76A",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    }
  ],
  "shapes": [
    {
      "id": "triangle-1",
      "name": "Red Goal",
      "vertices": [
        {
          "x": 0,
          "y": 70
        },
        {
          "x": 0,
          "y": 144
        },
        {
          "x": 24,
          "y": 144
        },
        {
          "x": 6,
          "y": 119
        },
        {
          "x": 6,
          "y": 70
        }
      ],
      "color": "#dc2626",
      "fillColor": "#ff6b6b"
    },
    {
      "id": "triangle-2",
      "name": "Blue Goal",
      "vertices": [
        {
          "x": 138,
          "y": 119
        },
        {
          "x": 119,
          "y": 144
        },
        {
          "x": 144,
          "y": 144
        },
        {
          "x": 144,
          "y": 70
        },
        {
          "x": 137,
          "y": 70
        }
      ],
      "color": "#2563eb",
      "fillColor": "#60a5fa"
    }
  ],
  "sequence": [
    {
      "kind": "path",
      "lineId": "mk5zimhe-rmc7dy"
    },
    {
      "kind": "wait",
      "id": "mk5zl31f-9w6pyy",
      "name": "Launch",
      "durationMs": 2000,
      "locked": false
    },
    {
      "kind": "path",
      "lineId": "mk6014zb-9qa4nj"
    },
    {
      "kind": "path",
      "lineId": "mk6025dt-du92xt"
    },
    {
      "kind": "path",
      "lineId": "ml9ytebc-zfuo86"
    },
    {
      "kind": "path",
      "lineId": "mk6054nm-53eoae"
    },
    {
      "kind": "wait",
      "id": "mkolo5iu-h2kox7",
      "name": "Launch",
      "durationMs": 2000,
      "locked": false
    },
    {
      "kind": "path",
      "lineId": "ml9yw9d2-5fzj6e"
    },
    {
      "kind": "path",
      "lineId": "ml9yxgbs-0oo0xp"
    },
    {
      "kind": "path",
      "lineId": "ml9yyvfk-xut3ac"
    }
  ],
  "settings": {
    "xVelocity": 75,
    "yVelocity": 65,
    "aVelocity": 3.141592653589793,
    "kFriction": 0.1,
    "rWidth": 16,
    "rHeight": 16,
    "safetyMargin": 1,
    "maxVelocity": 40,
    "maxAcceleration": 30,
    "maxDeceleration": 30,
    "fieldMap": "decode.webp",
    "robotImage": "/robot.png",
    "theme": "auto",
    "showGhostPaths": false,
    "showOnionLayers": false,
    "onionLayerSpacing": 3,
    "onionColor": "#dc2626",
    "onionNextPointOnly": false
  },
  "version": "1.2.1",
  "timestamp": "2026-02-05T21:40:17.298Z"
}