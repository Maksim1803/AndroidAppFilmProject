# Implementation Plan - Add Free Online Movie Links

Add links to resources where movies can be watched for free online in the movie details screen.

## User Review Required

> [!NOTE]
> The links will be generated based on the movie title and will point to search results on popular platforms (Google, YouTube, OK.ru). This ensures that links are always available even if specific direct links are not provided by the API.

## Proposed Changes

### UI Components

#### [MODIFY] [fragment_details.xml](file:///D:/Android_Studio_projects_2025/AndroidAppFilmProject/app/src/main/res/layout/fragment_details.xml)
- Add a header `TextView` with the text "Watch for free".
- Add a `LinearLayout` or `HorizontalScrollView` containing buttons for different resources (Google, YouTube, OK.ru).

#### [MODIFY] [strings.xml](file:///D:/Android_Studio_projects_2025/AndroidAppFilmProject/app/src/main/res/values/strings.xml)
- Add strings for the "Watch for free" header and button labels.

### Logic

#### [MODIFY] [DetailsFragment.kt](file:///D:/Android_Studio_projects_2025/AndroidAppFilmProject/app/src/main/java/com/example/androidappfilmproject/view/fragments/DetailsFragment.kt)
- Implement click listeners for the new buttons.
- Use `Intent.ACTION_VIEW` to open search URLs in the browser.

#### [MODIFY] [LocalDetailsFragment.kt](file:///D:/Android_Studio_projects_2025/AndroidAppFilmProject/app/src/main/java/com/example/androidappfilmproject/view/fragments/LocalDetailsFragment.kt)
- Implement the same click listeners for the local details screen.

## Verification Plan

### Manual Verification
- Deploy the app to a device or emulator.
- Open the details of any movie.
- Scroll down to the "Watch for free" section.
- Click on each button and verify that it opens the correct search results page in the browser with the movie title.
