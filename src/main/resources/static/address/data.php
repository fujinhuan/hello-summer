<?php

$level = $_GET['level'];
$code = $_GET['code'];

$data1 = array(
    array("code" => "1111111", "name" => "东城市1"),
    array("code" => "1111111", "name" => "东城市2"),
    array("code" => "1111111", "name" => "东城市3"),
    array("code" => "1111111", "name" => "东城市4"),
    array("code" => "1111111", "name" => "东城市5"),
);

$data2 = array(
    array("code" => "222222", "name" => "华门县1"),
    array("code" => "222222", "name" => "华门县2"),
    array("code" => "222222", "name" => "华门县3"),
    array("code" => "222222", "name" => "华门县4"),
    array("code" => "222222", "name" => "华门县5"),
    array("code" => "222222", "name" => "华门县6"),
);

$data3 = array(
    array("code" => "333333", "name" => "东门街道1"),
    array("code" => "333333", "name" => "东门街道2"),
    array("code" => "333333", "name" => "东门街道3"),
    array("code" => "333333", "name" => "东门街道4"),
    array("code" => "333333", "name" => "东门街道5"),
    array("code" => "333333", "name" => "东门街道6"),
);

if ($level == 1) {
    echo json_encode(array("status" => 1, "data" => $data1));
} else if ($level == 2) {
    echo json_encode(array("status" => 1, "data" => $data2));
} else if ($level == 3) {
    echo json_encode(array("status" => 1, "data" => $data3));
} else {
    echo json_encode(array("status" => 0, "data" => ""));
}
