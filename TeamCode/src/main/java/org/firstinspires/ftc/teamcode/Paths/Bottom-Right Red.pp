{
  "startPoint": {
    "x": 88,
    "y": 8,
    "heading": "linear",
    "startDeg": 90,
    "endDeg": 180,
    "locked": false
  },
  "lines": [
    {
      "id": "mk5zimhe-rmc7dy",
      "name": "Go to Launch zone",
      "endPoint": {
        "x": 94.5,
        "y": 93.5,
        "heading": "linear",
        "reverse": false,
        "startDeg": 90,
        "endDeg": 45
      },
      "controlPoints": [],
      "color": "#B7C8D6",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "mk5zlh9h-5pm8iy",
      "name": "move to artifacts row 1",
      "endPoint": {
        "x": 94.5,
        "y": 84.5,
        "heading": "linear",
        "reverse": false,
        "startDeg": 45,
        "endDeg": 0
      },
      "controlPoints": [],
      "color": "#B76D9C",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "mk5zmpu1-oo0oa5",
      "name": "pick up artifacts row 1",
      "endPoint": {
        "x": 116.8,
        "y": 84.5,
        "heading": "linear",
        "reverse": false,
        "startDeg": 0,
        "endDeg": 0
      },
      "controlPoints": [],
      "color": "#AAAC9A",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "mk5zzkn2-ecrobn",
      "name": "Go to Launch zone",
      "endPoint": {
        "x": 94.5,
        "y": 93.5,
        "heading": "linear",
        "reverse": false,
        "startDeg": 0,
        "endDeg": 45
      },
      "controlPoints": [],
      "color": "#D58BAD",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "mk6014zb-9qa4nj",
      "name": "Down to artifacts 2",
      "endPoint": {
        "x": 94.5,
        "y": 60,
        "heading": "linear",
        "reverse": false,
        "startDeg": 45,
        "endDeg": 0
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
        "x": 116.8,
        "y": 60,
        "heading": "linear",
        "reverse": false,
        "startDeg": 0,
        "endDeg": 0
      },
      "controlPoints": [],
      "color": "#A65BC8",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "mk6054nm-53eoae",
      "name": "Up to launch zone",
      "endPoint": {
        "x": 94.5,
        "y": 93.5,
        "heading": "linear",
        "reverse": false,
        "startDeg": 0,
        "endDeg": 45
      },
      "controlPoints": [],
      "color": "#A577A7",
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
          "x": 144,
          "y": 70
        },
        {
          "x": 144,
          "y": 144
        },
        {
          "x": 120,
          "y": 144
        },
        {
          "x": 138,
          "y": 119
        },
        {
          "x": 138,
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
          "x": 6,
          "y": 119
        },
        {
          "x": 25,
          "y": 144
        },
        {
          "x": 0,
          "y": 144
        },
        {
          "x": 0,
          "y": 70
        },
        {
          "x": 7,
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
      "lineId": "mk5zlh9h-5pm8iy"
    },
    {
      "kind": "path",
      "lineId": "mk5zmpu1-oo0oa5"
    },
    {
      "kind": "path",
      "lineId": "mk5zzkn2-ecrobn"
    },
    {
      "kind": "wait",
      "id": "mk609yk5-jou2m0",
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
      "lineId": "mk6054nm-53eoae"
    },
    {
      "kind": "wait",
      "id": "mkolo5iu-h2kox7",
      "name": "Launch",
      "durationMs": 2000,
      "locked": false
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
  "timestamp": "2026-02-05T21:40:29.104Z"
}